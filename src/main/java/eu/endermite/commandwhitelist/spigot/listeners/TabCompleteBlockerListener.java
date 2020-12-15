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
        String cmd = buffer.replace(CommandsList.getLastArgument(buffer), "");
        List<String> blockedCommands = CommandsList.getSuggestions(player);
        List<String> suggestions = event.getCompletions();
        for (String s : blockedCommands) {
            String slast = CommandsList.getLastArgument(s);
            String scommand = s.replace(slast, "");
            cmd = cmd.replace(CommandsList.getLastArgument(cmd), "");
            if (cmd.startsWith("/" + scommand + " ")) {
                continue;
            }
            try {
                suggestions.remove(slast);
            } catch (Exception ignored) {}
        }
        event.setCompletions(suggestions);
    }
}
