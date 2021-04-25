package eu.endermite.commandwhitelist.waterfall;

import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.waterfall.command.BungeeMainCommand;
import eu.endermite.commandwhitelist.waterfall.listeners.BungeeChatEventListener;
import eu.endermite.commandwhitelist.waterfall.listeners.WaterfallDefineCommandsListener;
import eu.endermite.commandwhitelist.waterfall.metrics.BungeeMetrics;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class CommandWhitelistWaterfall extends Plugin {

    private static CommandWhitelistWaterfall plugin;
    private static ConfigCache configCache;
    private static BungeeAudiences audiences;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("Running on "+ ChatColor.DARK_AQUA+getProxy().getName());
        loadConfig();
        audiences = BungeeAudiences.create(this);
        this.getProxy().getPluginManager().registerListener(this, new BungeeChatEventListener());
        try {
            Class.forName("io.github.waterfallmc.waterfall.conf.WaterfallConfiguration");
            this.getProxy().getPluginManager().registerListener(this, new WaterfallDefineCommandsListener());
        } catch (ClassNotFoundException e) {
            getLogger().severe(ChatColor.DARK_RED+"Bungee tab completion blocker requires Waterfall other Waterfall fork.");
        }

        getProxy().getPluginManager().registerCommand(this, new BungeeMainCommand("bcw"));

        int pluginId = 8704;
        new BungeeMetrics(this, pluginId);
    }

    public static CommandWhitelistWaterfall getPlugin() {
        return plugin;
    }
    public static ConfigCache getConfigCache() {
        return configCache;
    }

    public static BungeeAudiences getAudiences() {
        return audiences;
    }

    public void loadConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            configCache = new ConfigCache(new File(getDataFolder(), "config.yml"), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfigAsync(CommandSender sender) {
        getProxy().getScheduler().runAsync(this, () -> {
            loadConfig();
            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().config_reloaded));
        });
    }

    /**
     * @param player Bungee Player
     * @return commands available to the player
     */
    public static HashSet<String> getCommands(ProxiedPlayer player, HashMap<String, CWGroup> groups) {
        HashSet<String> commandList = new HashSet<>();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue().getCommands());
            else if (player.hasPermission("commandwhitelist.group." + s.getKey()))
                commandList.addAll(s.getValue().getCommands());
        }
        return commandList;
    }

    /**
     * @param player Bungee Player
     * @return subcommands unavailable for the player
     */
    public static HashSet<String> getSuggestions(ProxiedPlayer player, HashMap<String, CWGroup> groups) {
        HashSet<String> suggestionList = new HashSet<>();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                suggestionList.addAll(s.getValue().getSubCommands());
            if (player.hasPermission("commandwhitelist.group." + s.getKey()))
                continue;
            suggestionList.addAll(s.getValue().getSubCommands());
        }
        return suggestionList;
    }
}
