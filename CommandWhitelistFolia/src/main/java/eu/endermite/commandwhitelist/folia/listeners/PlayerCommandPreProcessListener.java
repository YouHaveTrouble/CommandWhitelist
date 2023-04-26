package eu.endermite.commandwhitelist.folia.listeners;

import eu.endermite.commandwhitelist.folia.CommandWhitelistFolia;
import eu.endermite.commandwhitelist.common.CWPermission;
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
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;
        String caseSensitiveLabel = CommandUtil.getCommandLabel(event.getMessage());
        String label = caseSensitiveLabel.toLowerCase();

        String fullCommand = event.getMessage().substring(label.length()+1);
        fullCommand = "/"+label+fullCommand;

        event.setMessage(fullCommand);
        BukkitAudiences audiences = CommandWhitelistFolia.getAudiences();
        ConfigCache config = CommandWhitelistFolia.getConfigCache();

        HashSet<String> commands = CommandWhitelistFolia.getCommands(player);
        if (!commands.contains(label)) {
            event.setCancelled(true);
            audiences.player(player).sendMessage(CWCommand.miniMessage.deserialize(config.prefix + CommandWhitelistFolia.getCommandDeniedMessage(label)));
            return;
        }

        HashSet<String> bannedSubCommands = CommandWhitelistFolia.getSuggestions(player);
        for (String bannedSubCommand : bannedSubCommands) {
            if (event.getMessage().toLowerCase().substring(1).startsWith(bannedSubCommand)) {
                event.setCancelled(true);
                audiences.player(player).sendMessage(CWCommand.miniMessage.deserialize(config.prefix + config.subcommand_denied));
                return;
            }
        }

    }
}