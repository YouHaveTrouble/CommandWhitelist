package eu.endermite.commandwhitelist.spigot;

import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.spigot.command.MainCommand;
import eu.endermite.commandwhitelist.spigot.listeners.*;
import eu.endermite.commandwhitelist.spigot.metrics.BukkitMetrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class CommandWhitelist extends JavaPlugin {

    private static CommandWhitelist commandWhitelist;
    private static ConfigCache configCache;
    private static boolean isLegacy;

    @Override
    public void onEnable() {

        commandWhitelist = this;

        reloadPluginConfig();

        Plugin protocollib = getServer().getPluginManager().getPlugin("ProtocolLib");

        if (!getConfigCache().useProtocolLib || protocollib == null || !protocollib.isEnabled()) {
            getServer().getPluginManager().registerEvents(new PlayerCommandPreProcessListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerCommandSendListener(), this);
        } else {
            PacketCommandSendListener.protocol(this);
            getLogger().info(ChatColor.AQUA + "Using ProtocolLib for command filter!");
        }
        getServer().getPluginManager().registerEvents(new TabCompleteBlockerListener(), this);


        getCommand("commandwhitelist").setExecutor(new MainCommand());

        int pluginId = 8705;
        new BukkitMetrics(this, pluginId);
    }

    public void reloadPluginConfig() {
        File configFile = new File("plugins/CommandWhitelist/config.yml");
        configCache = new ConfigCache(configFile, true);


    }

    public void reloadPluginConfig(CommandSender sender) {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            reloadPluginConfig();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.updateCommands();
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().prefix + CommandWhitelist.getConfigCache().config_reloaded));
        });
    }

    public static CommandWhitelist getPlugin() {
        return commandWhitelist;
    }

    public static ConfigCache getConfigCache() {
        return configCache;
    }
}
