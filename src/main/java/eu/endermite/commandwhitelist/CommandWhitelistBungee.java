package eu.endermite.commandwhitelist;

import com.google.common.io.ByteStreams;
import eu.endermite.commandwhitelist.config.BungeeConfigCache;
import eu.endermite.commandwhitelist.listeners.BungeeChatEventListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class CommandWhitelistBungee extends Plugin {

    private static eu.endermite.commandwhitelist.CommandWhitelistBungee plugin;
    private static BungeeConfigCache configCache;

    @Override
    public void onEnable() {

        plugin = this;

        loadConfig();

        this.getProxy().getPluginManager().registerListener(this, new BungeeChatEventListener());

    }

    public static eu.endermite.commandwhitelist.CommandWhitelistBungee getPlugin() {
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
