package eu.endermite.commandwhitelist.bukkit.listeners;

import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;

public class PlayerCommandSendListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("commandwhitelist.bypass")) return;
        HashSet<String> commandList = CommandWhitelistBukkit.getCommands(player);
        event.getCommands().removeIf((cmd) -> !commandList.contains(cmd));
    }
}
