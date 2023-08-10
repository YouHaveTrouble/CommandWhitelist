package me.youhavetrouble.commandwhitelist.bukkit.listeners;

import me.youhavetrouble.commandwhitelist.bukkit.CommandWhitelistBukkit;
import me.youhavetrouble.commandwhitelist.common.CWCommandEntry;
import me.youhavetrouble.commandwhitelist.common.CWPermission;
import me.youhavetrouble.commandwhitelist.common.CWPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandTabCompleteListener implements Listener {

    private final CommandWhitelistBukkit plugin;

    public CommandTabCompleteListener(CommandWhitelistBukkit plugin ) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommandTabComplete(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        Player player = (Player) event.getSender();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        String buffer = event.getBuffer();

        if ((buffer.split(" ").length == 1 && !buffer.endsWith(" ")) || !buffer.startsWith("/")) {
            plugin.getCWConfig().debug("Actively prevented "+event.getSender().getName()+"'s tab completion (sus packet)");
            event.setCancelled(true);
            return;
        }

        if (event.getCompletions().isEmpty()) return;

        String[] split = buffer.split(" ");
        if (split.length == 0) return;
        String[] splitWithoutLastArg = new String[split.length - 1];
        System.arraycopy(split, 0, splitWithoutLastArg, 0, splitWithoutLastArg.length);
        String commandWithoutLastArg = String.join(" ", splitWithoutLastArg);

        CWPlayer cwPlayer = new CWPlayer(player);
        List<CWCommandEntry> validCompletions = new ArrayList<>();
        for (CWCommandEntry entry : cwPlayer.getCommands(plugin.getCWConfig())) {
            if (!entry.partiallyMatches(commandWithoutLastArg)) continue;
            validCompletions.add(entry);
        }

        int index = split.length - 1;

        List<String> completions = event.getCompletions();
        Iterator<String> iterator = completions.iterator();
        while (iterator.hasNext()) {
            String completion = iterator.next();
            for (CWCommandEntry entry : validCompletions) {
                if (entry.argumentMatches(completion, index)) continue;
                iterator.remove();
                break;
            }
        }

        event.setCompletions(completions);
    }

}
