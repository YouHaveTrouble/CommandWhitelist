package eu.endermite.commandwhitelist.bukkit;

import eu.endermite.commandwhitelist.bukkit.command.MainCommandExecutor;
import eu.endermite.commandwhitelist.bukkit.listeners.*;
import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.ConfigCache;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;

public class CommandWhitelistBukkit extends JavaPlugin {

    private static CommandWhitelistBukkit commandWhitelist;
    private static ConfigCache configCache;
    private static BukkitAudiences audiences;

    @Override
    public void onEnable() {

        commandWhitelist = this;
        audiences = BukkitAudiences.create(this);

        reloadPluginConfig();

        Plugin protocollib = getServer().getPluginManager().getPlugin("ProtocolLib");

        if (!getConfigCache().useProtocolLib || protocollib == null || !protocollib.isEnabled()) {
            getServer().getPluginManager().registerEvents(new PlayerCommandPreProcessListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerCommandSendListener(), this);
        } else {
            PacketCommandPreProcessListener.protocol(this);
            getLogger().info(ChatColor.AQUA + "Using ProtocolLib for command filter!");
        }
        try {
            Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
            getServer().getPluginManager().registerEvents(new AsyncTabCompleteBlockerListener(), this);
        } catch (ClassNotFoundException e) {
            getServer().getPluginManager().registerEvents(new TabCompleteBlockerListener(), this);
        }


        PluginCommand command = getCommand("commandwhitelist");
        if (command != null) {
            MainCommandExecutor executor = new MainCommandExecutor();
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        new Metrics(this, 8705);
    }

    private void reloadPluginConfig() {
        File configFile = new File("plugins/CommandWhitelist/config.yml");
        if (configCache == null)
            configCache = new ConfigCache(configFile, true, getSLF4JLogger());
        else
            configCache.reloadConfig();
    }

    public void reloadPluginConfig(CommandSender sender) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            reloadPluginConfig();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.updateCommands();
            }
            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(configCache.prefix + configCache.config_reloaded));
        });
    }

    public static CommandWhitelistBukkit getPlugin() {
        return commandWhitelist;
    }

    public static ConfigCache getConfigCache() {
        return configCache;
    }

    public static BukkitAudiences getAudiences() {
        return audiences;
    }

    /**
     * @param player Bukkit Player
     * @return commands available to the player
     */
    public static HashSet<String> getCommands(org.bukkit.entity.Player player) {
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
     * @param player Bukkit Player
     * @return subcommands unavailable for the player
     */
    public static HashSet<String> getSuggestions(org.bukkit.entity.Player player) {
        HashSet<String> suggestionList = new HashSet<>();
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                suggestionList.addAll(s.getValue().getSubCommands());
            if (player.hasPermission(s.getValue().getPermission())) continue;
            suggestionList.addAll(s.getValue().getSubCommands());
        }
        return suggestionList;
    }
}
