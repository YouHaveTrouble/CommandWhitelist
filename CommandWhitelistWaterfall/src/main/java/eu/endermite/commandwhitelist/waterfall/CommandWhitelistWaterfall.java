package eu.endermite.commandwhitelist.waterfall;

import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.waterfall.command.BungeeMainCommand;
import eu.endermite.commandwhitelist.waterfall.listeners.BungeeChatEventListener;
import eu.endermite.commandwhitelist.waterfall.listeners.BungeeTabcompleteListener;
import eu.endermite.commandwhitelist.waterfall.listeners.WaterfallDefineCommandsListener;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;

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
        Metrics metrics = new Metrics(this, 8704);

        this.getProxy().getPluginManager().registerListener(this, new BungeeChatEventListener());
        try {
            Class.forName("io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent");
            metrics.addCustomChart(new SimplePie("proxy", () -> "Waterfall"));
            this.getProxy().getPluginManager().registerListener(this, new WaterfallDefineCommandsListener());
        } catch (ClassNotFoundException e) {
            metrics.addCustomChart(new SimplePie("proxy", () -> "Bungee"));
            getLogger().severe("Bungee command completion blocker requires Waterfall other Waterfall fork.");
        }
        this.getProxy().getPluginManager().registerListener(this, new BungeeTabcompleteListener());
        getProxy().getPluginManager().registerCommand(this, new BungeeMainCommand("bcw"));


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
        if (configCache == null)
            configCache = new ConfigCache(new File(getDataFolder(), "config.yml"), false, getLogger());
        else
            configCache.reloadConfig();
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
    public static HashSet<String> getCommands(ProxiedPlayer player) {
        HashSet<String> commandList = new HashSet<>();
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue().getCommands());
            else if (player.hasPermission(s.getValue().getPermission()))
                commandList.addAll(s.getValue().getCommands());
        }
        return commandList;
    }

    /**
     * @param player Bungee Player
     * @return subcommands unavailable for the player
     */
    public static HashSet<String> getSuggestions(ProxiedPlayer player) {
        HashMap<String, CWGroup> groups = configCache.getGroupList();
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
