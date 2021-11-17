package eu.endermite.commandwhitelist.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;


import java.util.HashSet;

public class AsyncCommandSendListener implements Listener {
    @EventHandler
    public void AsyncPlayerSendCommandsEvent(AsyncPlayerSendCommandsEvent<?> event){
        if(!event.isAsynchronous() || event.hasFiredAsync()) return;
        Player player = event.getPlayer();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        HashSet<String> commandList = CommandWhitelistBukkit.getCommands(player);
        event.getCommandNode().getChildren().removeIf(commandNode -> !commandList.contains(commandNode.getName()));
    }
}
