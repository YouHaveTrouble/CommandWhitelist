package eu.endermite.commandwhitelist.bukkit.listeners;

import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TabCompleteBlockerListener implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandTabComplete(org.bukkit.event.server.TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        Player player = (Player) event.getSender();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        event.setCompletions(
                CommandUtil.filterSuggestions(
                    event.getBuffer(),
                    event.getCompletions(),
                    CommandWhitelistBukkit.getSuggestions(player)
                )
        );
    }
}
