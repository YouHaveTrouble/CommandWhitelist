package eu.endermite.commandwhitelist;

import eu.endermite.commandwhitelist.config.ConfigCache;
import eu.endermite.commandwhitelist.listeners.PlayerCommandPreProcess;
import eu.endermite.commandwhitelist.listeners.PlayerCommandSend;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandWhitelist extends JavaPlugin {

    private static CommandWhitelist commandWhitelist;
    private static ConfigCache configCache;

    @Override
    public void onEnable() {

        commandWhitelist = this;
        reloadPluginConfig();

        getServer().getPluginManager().registerEvents(new PlayerCommandPreProcess(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandSend(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void reloadPluginConfig() {
        saveDefaultConfig();
        configCache = new ConfigCache(this.getConfig());
    }

    public static CommandWhitelist getPlugin() {return commandWhitelist;}
    public static ConfigCache getConfigCache() {return configCache;}
}
