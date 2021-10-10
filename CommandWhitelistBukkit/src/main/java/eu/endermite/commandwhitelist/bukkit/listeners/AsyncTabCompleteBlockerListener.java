package eu.endermite.commandwhitelist.bukkit.listeners;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
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
        player.sendMessage("async");
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        player.sendMessage("1");
        String buffer = event.getBuffer();
        if (!buffer.endsWith(" ") && buffer.split(" ").length == 1) event.setCancelled(true);
        player.sendMessage("2");
        if (event.getCompletions().isEmpty()) return;
        player.sendMessage("3");
        event.setCompletions(CommandUtil.filterSuggestions(buffer, event.getCompletions(), CommandWhitelistBukkit.getSuggestions(player)));
    }

}
