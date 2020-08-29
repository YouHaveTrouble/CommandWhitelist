package eu.endermite.commandwhitelist.bungee.listeners;

import eu.endermite.commandwhitelist.bungee.CommandWhitelistBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Map;

public class BungeeChatEventListener implements Listener {

    @EventHandler
    public void onChatEvent(net.md_5.bungee.api.event.ChatEvent event) {

        if (event.isCancelled())
            return;

        if (!(event.getSender() instanceof ProxiedPlayer))
            return;

        if (!event.isProxyCommand())
            return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (player.hasPermission("commandwhitelist.bypass")) {
            return;
        }

        String command = event.getMessage().toLowerCase();
        boolean found = false;
        for (Map.Entry<String, List<String>> s : CommandWhitelistBungee.getConfigCache().getPermList().entrySet()) {
            if (!player.hasPermission("commandwhitelist.commands." + s.getKey()))
                continue;

            for (String comm : s.getValue()) {
                comm = comm.toLowerCase();
                if (command.equalsIgnoreCase("/" + comm)) {
                    found = true;
                    break;
                } else if (command.startsWith("/" + comm + " ")) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelistBungee.getConfigCache().getPrefix() + CommandWhitelistBungee.getConfigCache().getCommandDenied()));
        }
    }
}
