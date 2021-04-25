package eu.endermite.commandwhitelist.bukkit.listeners;

import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.ConfigCache;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import java.util.HashSet;

public class PlayerCommandPreProcessListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommandSendEvent(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("commandwhitelist.bypass"))
            return;
        String label = CommandUtil.getCommandLabel(event.getMessage().toLowerCase());

        ConfigCache configCache = CommandWhitelistBukkit.getConfigCache();
        BukkitAudiences audiences = CommandWhitelistBukkit.getAudiences();

        HashSet<String> commands = CommandWhitelistBukkit.getCommands(player, configCache.getGroupList());
        if (!commands.contains(label)) {
            event.setCancelled(true);
            ConfigCache config = CommandWhitelistBukkit.getConfigCache();
            audiences.player(player).sendMessage(MiniMessage.markdown().parse(config.prefix + config.command_denied));
            return;
        }

        HashSet<String> bannedSubCommands = CommandWhitelistBukkit.getSuggestions(player, configCache.getGroupList());
        for (String bannedSubCommand : bannedSubCommands) {
            if (event.getMessage().toLowerCase().substring(1).startsWith(bannedSubCommand)) {
                event.setCancelled(true);
                ConfigCache config = CommandWhitelistBukkit.getConfigCache();
                audiences.player(player).sendMessage(MiniMessage.markdown().parse(config.prefix + config.subcommand_denied));
                return;
            }
        }


    }
}