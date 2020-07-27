package eu.endermite.commandwhitelist.listeners;

import eu.endermite.commandwhitelist.CommandWhitelist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.*;

public class PlayerCommandSendListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandSendEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("commandwhitelist.bypass")) {
            return;
        }

        List<String> commandList = new ArrayList<>();

        for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermList().entrySet()) {
                if (player.hasPermission("commandwhitelist.commands."+s.getKey())) {
                    commandList.addAll(s.getValue());
                }
        }

        event.getCommands().removeIf((cmd) -> !commandList.contains(cmd));

    }



}
