package eu.endermite.commandwhitelist.spigot.listeners;

import eu.endermite.commandwhitelist.api.CommandsList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.*;

public class PlayerCommandSendListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("commandwhitelist.bypass"))
            return;
        List<String> commandList = CommandsList.getCommands(player);
        event.getCommands().removeIf((cmd) -> !commandList.contains(cmd));
    }
}
