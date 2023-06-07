package me.youhavetrouble.commandwhitelist.bukkit;

import me.youhavetrouble.commandwhitelist.bukkit.listeners.CommandSendListener;
import me.youhavetrouble.commandwhitelist.common.CWCommandEntry;
import me.youhavetrouble.commandwhitelist.common.CWGroup;
import me.youhavetrouble.commandwhitelist.common.ConfigCache;
import me.youhavetrouble.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class CommandWhitelistBukkit extends JavaPlugin {

    private ConfigCache configCache;
    private BukkitAudiences audiences;

    @Override
    public void onEnable() {
        reloadPluginConfig();
        audiences = BukkitAudiences.create(this);
        getServer().getPluginManager().registerEvents(new CommandSendListener(this), this);
    }

    public Set<CWCommandEntry> getCommands(Player player) {
        HashSet<CWCommandEntry> commands = new HashSet<>();
        Map<String, CWGroup> groups = configCache.getGroupList();
        for (Map.Entry<String, CWGroup> groupEntry : groups.entrySet()) {
            CWGroup group = groupEntry.getValue();
            String groupId = groupEntry.getKey();
            if (groupId.equalsIgnoreCase("default")) commands.addAll(group.getCommands());
            else if (player.hasPermission(group.getPermission())) commands.addAll(group.getCommands());
        }
        return commands;
    }

    private void reloadPluginConfig() {
        File configFile = new File("plugins/CommandWhitelist/config.yml");
        if (configCache != null) {
            configCache.reloadConfig();
            return;
        }
        try {
            configCache = new ConfigCache(configFile, true, getSLF4JLogger());
        } catch (NoSuchMethodError e) {
            configCache = new ConfigCache(configFile, true, null);
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
            audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(configCache.prefix + configCache.config_reloaded));
        });
    }

}
