package eu.endermite.commandwhitelist.spigot.listeners;

import eu.endermite.commandwhitelist.api.RandomStuff;
import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import eu.endermite.commandwhitelist.spigot.config.ConfigCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

public class PlayerCommandPreProcessListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("commandwhitelist.bypass"))
            return;

        String command = event.getMessage().toLowerCase();

        for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermList().entrySet()) {
            if (!player.hasPermission("commandwhitelist.commands." + s.getKey()))
                continue;

            for (String comm : s.getValue()) {
                comm = comm.toLowerCase();
                if (command.equalsIgnoreCase("/" + comm))
                    return;
                else if (command.startsWith("/" + comm + " ")) {
                    return;
                }
            }
        }
        event.setCancelled(true);
        ConfigCache config = CommandWhitelist.getConfigCache();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + RandomStuff.getMessage(config.getCommandDeniedList(), config.getCommandDenied())));
    }

}
