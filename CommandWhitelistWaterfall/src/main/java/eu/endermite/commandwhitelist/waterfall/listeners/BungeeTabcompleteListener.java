package eu.endermite.commandwhitelist.waterfall.listeners;

import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.waterfall.CommandWhitelistWaterfall;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeeTabcompleteListener implements Listener {

    @EventHandler
    public void onTabcomplete(net.md_5.bungee.api.event.TabCompleteEvent event) {
        if (!(event.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
        if (event.getSuggestions().isEmpty()) return;
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        CommandUtil.filterSuggestions(
                event.getCursor(),
                event.getSuggestions(),
                CommandWhitelistWaterfall.getSuggestions(player)
        );
    }

}
