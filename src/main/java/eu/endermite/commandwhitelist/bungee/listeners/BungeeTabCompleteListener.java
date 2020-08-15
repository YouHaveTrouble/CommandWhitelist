package eu.endermite.commandwhitelist.bungee.listeners;

import eu.endermite.commandwhitelist.api.CommandsList;
import eu.endermite.commandwhitelist.bungee.CommandWhitelistBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;

public class BungeeTabCompleteListener implements Listener {

    @EventHandler
    public void onProxyDefineCommandsEvent(io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent event) {

        if (event.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

            if (player.hasPermission("commandwhitelist.bypass")) {
                return;
            }

            HashMap<String, Command> commandHashMap = new HashMap<>();
            CommandsList.getCommands(player).forEach(cmdName ->
                    CommandWhitelistBungee.getPlugin().getProxy().getPluginManager().getCommands()
                            .stream()
                            .filter(commandEntry -> cmdName.equalsIgnoreCase(commandEntry.getValue().getName()))
                            .forEach(commandEntry -> commandHashMap.put(commandEntry.getKey(), commandEntry.getValue())));

            event.getCommands().values().removeIf((cmd) -> !commandHashMap.containsValue(cmd));
        }


    }

}
