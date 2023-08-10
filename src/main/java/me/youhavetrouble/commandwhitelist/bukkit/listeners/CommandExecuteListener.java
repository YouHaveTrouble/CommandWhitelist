package me.youhavetrouble.commandwhitelist.bukkit.listeners;

import me.youhavetrouble.commandwhitelist.bukkit.CommandWhitelistBukkit;
import me.youhavetrouble.commandwhitelist.common.CWCommandEntry;
import me.youhavetrouble.commandwhitelist.common.CWPermission;
import me.youhavetrouble.commandwhitelist.common.CWPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandExecuteListener implements Listener {

    private final CommandWhitelistBukkit plugin;

    public CommandExecuteListener(CommandWhitelistBukkit plugin ) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        CWPlayer cwPlayer = new CWPlayer(player);
        for (CWCommandEntry entry : cwPlayer.getCommands(plugin.getCWConfig())) {
            if (entry.matches(event.getMessage())) return;
        }
        event.setCancelled(true);
    }


}
