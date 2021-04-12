package eu.endermite.commandwhitelist.spigot.listeners;

import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.CommandsList;
import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import eu.endermite.commandwhitelist.common.ConfigCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.List;
import java.util.Map;

public class PlayerCommandPreProcessListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("commandwhitelist.bypass"))
            return;
        String command = event.getMessage().toLowerCase();
        for (Map.Entry<String, CWGroup> s : CommandWhitelist.getConfigCache().getGroupList().entrySet()) {
            if (!player.hasPermission("commandwhitelist.commands." + s.getKey()))
                continue;
            for (String comm : s.getValue().getCommands()) {
                comm = comm.toLowerCase();
                if (command.equalsIgnoreCase("/" + comm) || command.startsWith("/" + comm + " ")) {
                    String rawCmd = event.getMessage();
                    List<String> bannedSubCommands = CommandsList.getSuggestions(player);
                    for (String bannedSubCommand : bannedSubCommands) {
                        if (rawCmd.startsWith("/"+bannedSubCommand)) {
                            event.setCancelled(true);
                            ConfigCache config = CommandWhitelist.getConfigCache();
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.prefix + config.subcommand_denied));
                            return;
                        }
                    }
                    return;
                }
            }
        }
        event.setCancelled(true);
        ConfigCache config = CommandWhitelist.getConfigCache();
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.prefix + config.command_denied));
    }
}
