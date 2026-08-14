package eu.endermite.commandwhitelist.bukkit.listeners;

import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public class TabCompleteBlockerListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCommandTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player player)) return;
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        String buffer = event.getBuffer();
        int commandEnd = buffer.indexOf(' ');
        if (commandEnd < 0 || buffer.charAt(0) != '/' || !CommandWhitelistBukkit.getCommands(player)
                .contains(buffer.substring(1, commandEnd).toLowerCase())) {
            CommandWhitelistBukkit.getConfigCache().debug("Actively prevented " + event.getSender().getName() + "'s tab completion (sus packet)");
            event.setCancelled(true);
            return;
        }
        if (event.getCompletions().isEmpty()) {
            return;
        }
        event.setCompletions(
                CommandUtil.filterSuggestions(
                        buffer,
                        event.getCompletions(),
                        CommandWhitelistBukkit.getSuggestions(player)
                )
        );
    }
}
