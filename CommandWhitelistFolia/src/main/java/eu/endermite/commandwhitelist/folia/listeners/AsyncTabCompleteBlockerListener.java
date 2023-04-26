package eu.endermite.commandwhitelist.folia.listeners;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import eu.endermite.commandwhitelist.folia.CommandWhitelistFolia;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AsyncTabCompleteBlockerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandTabComplete(AsyncTabCompleteEvent event) {
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
        event.setCompletions(CommandUtil.filterSuggestions(buffer, event.getCompletions(), CommandWhitelistFolia.getSuggestions(player)));
    }

}
