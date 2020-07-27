package eu.endermite.commandwhitelist;

import eu.endermite.commandwhitelist.command.MainCommand;
import eu.endermite.commandwhitelist.config.ConfigCache;
import eu.endermite.commandwhitelist.listeners.PlayerCommandPreProcess;
import eu.endermite.commandwhitelist.listeners.PlayerCommandSend;
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
        configCache = new ConfigCache();
    }

    public static CommandWhitelist getPlugin() {return commandWhitelist;}
    public static ConfigCache getConfigCache() {return configCache;}

    public static <T> T TODO(final String reason) {
        throw new RuntimeException(reason);
    }
}
