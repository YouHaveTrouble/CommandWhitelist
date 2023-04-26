package eu.endermite.commandwhitelist.folia.listeners;

import eu.endermite.commandwhitelist.folia.CommandWhitelistFolia;
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
        if (!(event.getSender() instanceof Player)) return;
        Player player = (Player) event.getSender();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        String buffer = event.getBuffer();
        if ((buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/")) {
            CommandWhitelistFolia.getConfigCache().debug("Actively prevented "+event.getSender().getName()+"'s tab completion (sus packet)");
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
                        CommandWhitelistFolia.getSuggestions(player)
                )
        );
    }
}
