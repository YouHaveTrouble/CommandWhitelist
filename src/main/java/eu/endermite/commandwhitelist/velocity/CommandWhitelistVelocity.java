package eu.endermite.commandwhitelist.velocity;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.endermite.commandwhitelist.common.CommandsList;
import eu.endermite.commandwhitelist.velocity.command.VelocityMainCommand;
import eu.endermite.commandwhitelist.velocity.config.VelocityConfigCache;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CommandWhitelistVelocity {

    private static CommandWhitelistVelocity plugin;
    private static ProxyServer server;
    private static VelocityConfigCache configCache;
    private static Path folder;

    @Inject
    public CommandWhitelistVelocity(ProxyServer server, Logger logger, @DataDirectory final Path folder) {
        CommandWhitelistVelocity.server = server;
        CommandWhitelistVelocity.folder = folder;
        CommandWhitelistVelocity.plugin = this;
    }

    private static Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (!file.exists()) {
            try (InputStream input = CommandWhitelistVelocity.class.getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
        return new Toml().read(file);
    }

    private static void reloadConfig() {
        configCache = new VelocityConfigCache(loadConfig(folder));
    }

    public static void reloadConfig(CommandSource source) {
        server.getScheduler().buildTask(plugin, () -> {
            reloadConfig();
            source.sendMessage(Identity.nil(), Component.text(getConfigCache().getConfigReloaded()));
        }).schedule();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reloadConfig();
        CommandMeta commandMeta = server.getCommandManager().metaBuilder("vcw").build();
        server.getCommandManager().register(commandMeta, new VelocityMainCommand());
    }

    @Subscribe
    public void onUserCommandSendEvent(PlayerAvailableCommandsEvent event) {
        if (event.getPlayer().hasPermission("commandwhitelist.bypass"))
            return;
        List<String> allowedCommands = CommandsList.getCommands(event.getPlayer());
        event.getRootNode().getChildren().removeIf((commandNode) ->
                 server.getCommandManager().hasCommand(commandNode.getName())
                        && !allowedCommands.contains(commandNode.getName())
        );
    }

    @Subscribe
    public void onUserCommandExecuteEvent(com.velocitypowered.api.event.command.CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player))
            return;
        Player player = (Player) event.getCommandSource();

        if (player.hasPermission("commandwhitelist.bypass"))
            return;

        List<String> allowedCommands = CommandsList.getCommands(player);
        String command = event.getCommand().split(" ")[0];
        if (server.getCommandManager().hasCommand(command)
                && !allowedCommands.contains(command))
            event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
    }

    public static VelocityConfigCache getConfigCache() {
        return configCache;
    }

}
