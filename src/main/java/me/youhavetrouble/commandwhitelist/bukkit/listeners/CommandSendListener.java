package me.youhavetrouble.commandwhitelist.bukkit.listeners;

import me.youhavetrouble.commandwhitelist.bukkit.CommandWhitelistBukkit;
import me.youhavetrouble.commandwhitelist.common.CWCommandEntry;
import me.youhavetrouble.commandwhitelist.common.CWPermission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Iterator;

public class CommandSendListener implements Listener {

    private final CommandWhitelistBukkit plugin;

    public CommandSendListener(CommandWhitelistBukkit plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        Iterator<String> iterator = event.getCommands().iterator();
        while (iterator.hasNext()) {
            String command = iterator.next();
            for (CWCommandEntry entry : plugin.getCommands(player)) {
                if (entry.argumentMatches(command, 0)) continue;
                iterator.remove();
                break;
            }
        }
    }

}
