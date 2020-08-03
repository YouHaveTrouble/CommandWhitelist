package eu.endermite.commandwhitelist.bungee.listeners;

import eu.endermite.commandwhitelist.api.CommandsList;
import eu.endermite.commandwhitelist.bungee.CommandWhitelistBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BungeeTabCompleteListener implements Listener {

    @EventHandler
    public void onProxyDefineCommandsEvent(io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent event) {

        if (event.getReceiver() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

            if (player.hasPermission("commandwhitelist.bypass")) {
                return;
            }

            List<String> commandList = CommandsList.getCommands(player);

            HashMap<String, Command> commandHashMap = new HashMap<>();
            for (String s : commandList) {
                for (Map.Entry<String, Command> command : CommandWhitelistBungee.getPlugin().getProxy().getPluginManager().getCommands()) {
                    if (s.equalsIgnoreCase(command.getValue().getName())) {
                        commandHashMap.put(command.getKey(), command.getValue());
                    }
                }
            }
            event.getCommands().values().removeIf((cmd) -> !commandHashMap.containsValue(cmd));
        }


        
    }

}