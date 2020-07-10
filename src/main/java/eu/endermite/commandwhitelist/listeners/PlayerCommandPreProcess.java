package eu.endermite.commandwhitelist.listeners;

import eu.endermite.commandwhitelist.CommandWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

public class PlayerCommandPreProcess implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("commandwhitelist.bypass")) {
            return;
        }

        String command = event.getMessage();

        for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermList().entrySet()) {
            if (player.hasPermission("commandwhitelist.commands." + s.getKey())) {
                for (String comm : s.getValue()) {
                    if (command.startsWith("/" + comm)) {
                        return;
                    }
                }
            }
        }
        event.setCancelled(true);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + " " + CommandWhitelist.getConfigCache().getCommandDenied()));

    }

}
