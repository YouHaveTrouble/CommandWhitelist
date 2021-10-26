package eu.endermite.commandwhitelist.bukkit.listeners;

import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashSet;

public class PlayerCommandPreProcessListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("commandwhitelist.bypass")) return;
        String label = CommandUtil.getCommandLabel(event.getMessage().toLowerCase());
        BukkitAudiences audiences = CommandWhitelistBukkit.getAudiences();
        ConfigCache config = CommandWhitelistBukkit.getConfigCache();

        HashSet<String> commands = CommandWhitelistBukkit.getCommands(player);
        if (!commands.contains(label)) {
            event.setCancelled(true);
            String customCommandDeniedMessage = CommandWhitelistBukkit.getCommandDeniedMessage(label);
            if (!customCommandDeniedMessage.equals("")) {
                audiences.player(player).sendMessage(CWCommand.miniMessage.parse(config.prefix + customCommandDeniedMessage));
            } else {
                audiences.player(player).sendMessage(CWCommand.miniMessage.parse(config.prefix + config.command_denied));
            }
            return;
        }

        HashSet<String> bannedSubCommands = CommandWhitelistBukkit.getSuggestions(player);
        for (String bannedSubCommand : bannedSubCommands) {
            if (event.getMessage().toLowerCase().substring(1).startsWith(bannedSubCommand)) {
                event.setCancelled(true);
                audiences.player(player).sendMessage(CWCommand.miniMessage.parse(config.prefix + config.subcommand_denied));
                return;
            }
        }


    }
}