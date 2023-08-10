package me.youhavetrouble.commandwhitelist.bukkit;

import me.youhavetrouble.commandwhitelist.bukkit.listeners.CommandExecuteListener;
import me.youhavetrouble.commandwhitelist.bukkit.listeners.CommandSendListener;
import me.youhavetrouble.commandwhitelist.bukkit.listeners.CommandTabCompleteListener;
import me.youhavetrouble.commandwhitelist.common.CWConfig;
import me.youhavetrouble.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class CommandWhitelistBukkit extends JavaPlugin {

    private CWConfig CWConfig;
    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        reloadPluginConfig();
        audiences = BukkitAudiences.create(this);
        getServer().getPluginManager().registerEvents(new CommandSendListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandExecuteListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandTabCompleteListener(this), this);
    }

    private void reloadPluginConfig() {
        File configFile = new File("plugins/CommandWhitelist/config.yml");
        if (CWConfig != null) {
            CWConfig.reloadConfig();
            return;
        }
        try {
            CWConfig = new CWConfig(configFile, true, getSLF4JLogger());
        } catch (NoSuchMethodError e) {
            CWConfig = new CWConfig(configFile, true, null);
        }
    }

    public CompletableFuture<Void> reloadPluginConfig(CommandSender sender) {
        return CompletableFuture.runAsync(() -> {
            reloadPluginConfig();
            try {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.updateCommands();
                }
            } catch (Exception ignored) {}
            audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(CWConfig.prefix + CWConfig.config_reloaded));
        });
    }

    public CWConfig getCWConfig() {
        return CWConfig;
    }

}
