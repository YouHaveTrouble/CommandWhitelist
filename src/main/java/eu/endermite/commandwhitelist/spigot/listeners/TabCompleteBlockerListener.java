package eu.endermite.commandwhitelist.spigot.listeners;

import eu.endermite.commandwhitelist.api.CommandsList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.List;

public class TabCompleteBlockerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommandTabComplete(org.bukkit.event.server.TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player))
            return;
        Player player = (Player) event.getSender();
        String buffer = event.getBuffer();

        List<String> blockedCommands = CommandsList.getSuggestions(player);
        List<String> suggestions = event.getCompletions();

        for (String s : blockedCommands) {
            String slast = CommandsList.getLastArgument(s);
            String scommand = s.replace(slast, "");
            String[] cmdSplit = buffer.split(" ");
            StringBuilder cmdBuilder = new StringBuilder();
            for (int i = 0; i <= cmdSplit.length-1; i++)
                cmdBuilder.append(cmdSplit[i]).append(" ");

            String cmd = cmdBuilder.toString();
            if (cmd.startsWith("/"+scommand)) {
                // This sometimes throws exceptions. No clue why, it just does. try/catch is the only fix.
                // Probably happening when plugin adds suggestions in this event on the same priority - not confirmed.
                try {
                    while (suggestions.contains(slast))
                        suggestions.remove(slast);
                } catch (Exception ignored) {}
            }
        }
        event.setCompletions(suggestions);
    }
}
