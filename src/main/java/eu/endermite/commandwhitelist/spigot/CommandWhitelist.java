package eu.endermite.commandwhitelist.spigot;

import eu.endermite.commandwhitelist.spigot.command.MainCommand;
import eu.endermite.commandwhitelist.spigot.config.ConfigCache;
import eu.endermite.commandwhitelist.spigot.listeners.PlayerCommandPreProcessListener;
import eu.endermite.commandwhitelist.spigot.listeners.PlayerCommandSendListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandWhitelist extends JavaPlugin {

    private static CommandWhitelist commandWhitelist;
    private static ConfigCache configCache;

    @Override
    public void onEnable() {

        commandWhitelist = this;
        reloadPluginConfig();

        getServer().getPluginManager().registerEvents(new PlayerCommandPreProcessListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandSendListener(), this);
        getCommand("commandwhitelist").setExecutor(new MainCommand());
        getCommand("commandwhitelist").setTabCompleter(new MainCommand());

    }

    public void reloadPluginConfig() {
        saveDefaultConfig();
        reloadConfig();
        configCache = new ConfigCache();
    }

    public void reloadPluginConfig(CommandSender sender) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            reloadPluginConfig();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.updateCommands();
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getConfigReloaded()));
        });
    }

    public static CommandWhitelist getPlugin() {return commandWhitelist;}
    public static ConfigCache getConfigCache() {return configCache;}
}
