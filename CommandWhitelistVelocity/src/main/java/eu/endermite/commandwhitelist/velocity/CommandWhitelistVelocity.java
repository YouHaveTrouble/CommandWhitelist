package eu.endermite.commandwhitelist.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mojang.brigadier.Command;
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
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.velocity.command.VelocityMainCommand;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CommandWhitelistVelocity {

    private final ProxyServer server;
    private ConfigCache configCache;
    private final Path folder;
    private final Logger logger;
    private final Metrics.Factory metricsFactory;
    private final Injector injector;

    @Inject
    public CommandWhitelistVelocity(
            ProxyServer server,
            Logger logger,
            @DataDirectory final Path folder,
            Metrics.Factory metricsFactory,
            Injector injector
    ) {
        this.server = server;
        this.folder = folder;
        this.logger = logger;
        this.metricsFactory = metricsFactory;
        this.injector = injector;
    }

    private void reloadConfig() {
        if (configCache == null)
            configCache = new ConfigCache(folder.resolve("config.yml").toFile(), false, logger);
        else
            configCache.reloadConfig();
    }

    public int reloadConfig(CommandSource source) {
        server.getScheduler().buildTask(this, () -> {
            reloadConfig();
            source.sendMessage(CWCommand.miniMessage.deserialize(getConfigCache().prefix + getConfigCache().config_reloaded));
        }).schedule();
        return Command.SINGLE_SUCCESS;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reloadConfig();
        injector.getInstance(VelocityMainCommand.class).register();
        Metrics metrics = metricsFactory.make(this, 8704);
        metrics.addCustomChart(new SimplePie("proxy", () -> "Velocity"));
    }

    @Subscribe
    @SuppressWarnings("UnstableApiUsage")
    public void onUserCommandSendEvent(PlayerAvailableCommandsEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        HashSet<String> allowedCommands = getCommands(player);
        event.getRootNode().getChildren().removeIf((commandNode) ->
                server.getCommandManager().hasCommand(commandNode.getName())
                        && !allowedCommands.contains(commandNode.getName())
        );
    }

    @Subscribe
    public void onUserCommandExecuteEvent(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player)) return;
        Player player = (Player) event.getCommandSource();

        if (player.hasPermission(CWPermission.BYPASS.permission())) return;

        // Workaround for velocity executing "/ command" as valid command
        String command = event.getCommand().trim();

        HashSet<String> allowedCommands = getCommands(player);
        String label = CommandUtil.getCommandLabel(command);
        if (server.getCommandManager().hasCommand(label) && !allowedCommands.contains(label))
            event.setResult(CommandExecuteEvent.CommandResult.forwardToServer());
    }

    public ConfigCache getConfigCache() {
        return configCache;
    }

    /**
     * @param player Velocity Player
     * @return commands available to the player
     */
    public HashSet<String> getCommands(Player player) {
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        HashSet<String> commandList = new HashSet<>();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            CWGroup group = s.getValue();
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(group.getCommands());
            else if (player.hasPermission(group.getPermission()))
                commandList.addAll(group.getCommands());
        }
        return commandList;
    }

    /**
     * @param player Velocity Player
     * @return subcommands unavailable for the player
     */
    public HashSet<String> getSuggestions(Player player) {
        HashSet<String> suggestionList = new HashSet<>();
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                suggestionList.addAll(s.getValue().getSubCommands());
            if (!player.hasPermission(s.getValue().getPermission())) continue;
            suggestionList.addAll(s.getValue().getSubCommands());
        }
        return suggestionList;
    }

    public ArrayList<String> getServerCommands() {
        return new ArrayList<>(server.getCommandManager().getAliases());
    }

}
