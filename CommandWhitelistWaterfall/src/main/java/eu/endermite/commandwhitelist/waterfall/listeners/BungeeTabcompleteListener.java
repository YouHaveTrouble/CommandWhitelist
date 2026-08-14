package eu.endermite.commandwhitelist.waterfall.listeners;

import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.waterfall.CommandWhitelistWaterfall;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class BungeeTabcompleteListener implements Listener {

    @EventHandler
    public void onTabcomplete(net.md_5.bungee.api.event.TabCompleteEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;

        String cursor = event.getCursor();
        int commandEnd = cursor.indexOf(' ');
        if (commandEnd >= 0 && cursor.charAt(0) == '/') {
            String command = cursor.substring(1, commandEnd).toLowerCase();
            if (CommandWhitelistWaterfall.getPlugin().getProxy().getPluginManager().isExecutableCommand(command, player)
                    && !CommandWhitelistWaterfall.getCommands(player).contains(command)) {
                event.setCancelled(true);
                return;
            }
        }
        if (event.getSuggestions().isEmpty()) return;

        List<String> suggestions = CommandUtil.filterSuggestions(
                event.getCursor(),
                event.getSuggestions(),
                CommandWhitelistWaterfall.getSuggestions(player)
        );
        event.getSuggestions().clear();
        event.getSuggestions().addAll(suggestions);
    }

}
