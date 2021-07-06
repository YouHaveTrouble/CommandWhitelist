package eu.endermite.commandwhitelist.velocity;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.velocity.command.VelocityMainCommand;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CommandWhitelistVelocity {

    private static CommandWhitelistVelocity plugin;
    private static ProxyServer server;
    private static ConfigCache configCache;
    private static Path folder;
    private static Logger logger;
    private final Metrics.Factory metricsFactory;

    @Inject
    public CommandWhitelistVelocity(ProxyServer server, Logger logger, @DataDirectory final Path folder, Metrics.Factory metricsFactory) {
        CommandWhitelistVelocity.server = server;
        CommandWhitelistVelocity.folder = folder;
        CommandWhitelistVelocity.plugin = this;
        CommandWhitelistVelocity.logger = logger;
        this.metricsFactory = metricsFactory;

    }

    private static void reloadConfig() {
        if (configCache == null)
            configCache = new ConfigCache(new File(String.valueOf(folder), "config.yml"), false, logger);
        else
            configCache.reloadConfig();
    }

    public static void reloadConfig(CommandSource source) {
        server.getScheduler().buildTask(plugin, () -> {
            reloadConfig();
            source.sendMessage(Identity.nil(), MiniMessage.markdown().parse(getConfigCache().prefix+getConfigCache().config_reloaded));
        }).schedule();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reloadConfig();
        CommandMeta commandMeta = server.getCommandManager().metaBuilder("vcw").build();
        server.getCommandManager().register(commandMeta, new VelocityMainCommand());
        Metrics metrics = metricsFactory.make(this, 8704);
        metrics.addCustomChart(new SimplePie("proxy", ()-> "Velocity"));
    }

    @Subscribe
    public void onUserCommandSendEvent(PlayerAvailableCommandsEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        HashSet<String> allowedCommands = CommandWhitelistVelocity.getCommands(player);
        event.getRootNode().getChildren().removeIf((commandNode) ->
                 server.getCommandManager().hasCommand(commandNode.getName())
                        && !allowedCommands.contains(commandNode.getName())
        );
    }

    @Subscribe
    public void onUserCommandExecuteEvent(com.velocitypowered.api.event.command.CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player)) return;
        Player player = (Player) event.getCommandSource();

        if (player.hasPermission(CWPermission.BYPASS.permission())) return;

        HashSet<String> allowedCommands = CommandWhitelistVelocity.getCommands(player);
        String command = event.getCommand().split(" ")[0];
        if (server.getCommandManager().hasCommand(command)
                && !allowedCommands.contains(command))
            event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
    }

    public static ConfigCache getConfigCache() {
        return configCache;
    }

    /**
     * @param player Velocity Player
     * @return commands available to the player
     */
    public static HashSet<String> getCommands(Player player) {
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        HashSet<String> commandList = new HashSet<>();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue().getCommands());
            else if (player.hasPermission(s.getValue().getPermission()))
                commandList.addAll(s.getValue().getCommands());
        }
        return commandList;
    }

    /**
     * @param player Velocity Player
     * @return subcommands unavailable for the player
     */
    public static HashSet<String> getSuggestions(Player player, HashMap<String, CWGroup> groups) {
        HashSet<String> suggestionList = new HashSet<>();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                suggestionList.addAll(s.getValue().getSubCommands());
            if (player.hasPermission(s.getValue().getPermission())) continue;
            suggestionList.addAll(s.getValue().getSubCommands());
        }
        return suggestionList;
    }

}
