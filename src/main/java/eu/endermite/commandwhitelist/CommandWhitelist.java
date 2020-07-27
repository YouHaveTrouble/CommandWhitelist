package eu.endermite.commandwhitelist;

import eu.endermite.commandwhitelist.command.MainCommand;
import eu.endermite.commandwhitelist.config.ConfigCache;
import eu.endermite.commandwhitelist.listeners.PlayerCommandPreProcess;
import eu.endermite.commandwhitelist.listeners.PlayerCommandSend;
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

        getServer().getPluginManager().registerEvents(new PlayerCommandPreProcess(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandSend(), this);
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
            // Don't ask why it's called twice, it somehow breaks if it's called only once.
            reloadPluginConfig();
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
