package eu.endermite.commandwhitelist.bungee;

import com.google.common.io.ByteStreams;
import eu.endermite.commandwhitelist.bungee.config.BungeeConfigCache;
import eu.endermite.commandwhitelist.bungee.listeners.BungeeChatEventListener;
import eu.endermite.commandwhitelist.bungee.listeners.BungeeTabCompleteListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public final class CommandWhitelistBungee extends Plugin {

    private static CommandWhitelistBungee plugin;
    private static BungeeConfigCache configCache;

    @Override
    public void onEnable() {

        plugin = this;
        getLogger().info("Running on "+ ChatColor.DARK_AQUA+getProxy().getName());
        loadConfig();
        this.getProxy().getPluginManager().registerListener(this, new BungeeChatEventListener());
        if (this.getProxy().getName().contains("Waterfall") || getProxy().getName().contains("FlameCord")) {
            this.getProxy().getPluginManager().registerListener(this, new BungeeTabCompleteListener());
        } else {
            getLogger().info("Bungee tab completion requires Waterfall, FlameCord or other Waterfall fork.");
        }


    }

    public static CommandWhitelistBungee getPlugin() {
        return plugin;
    }

    public static BungeeConfigCache getConfigCache() {
        return configCache;
    }


    public void loadConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            File file = new File(getDataFolder(), "config.yml");

            if (!file.exists()) {
                file.createNewFile();
                try (InputStream in = getResourceAsStream("bungeeconfig.yml");
                     OutputStream out = new FileOutputStream(file)) {
                    ByteStreams.copy(in, out);
                }
            }
            final Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
            configCache = new BungeeConfigCache(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfigAsync() {
        getProxy().getScheduler().runAsync(this, this::loadConfig);
    }

}
