package eu.endermite.commandwhitelist.waterfall.listeners;

import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.waterfall.CommandWhitelistWaterfall;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashSet;

public class BungeeChatEventListener implements Listener {

    @EventHandler
    public void onChatEvent(ChatEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;
        if (!event.isProxyCommand()) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (player.hasPermission(CWPermission.BYPASS.permission())) return;

        String command = event.getMessage().toLowerCase();
        if (command.startsWith("/"))
            command = command.substring(1);
        ConfigCache configCache = CommandWhitelistWaterfall.getConfigCache();
        BungeeAudiences audiences = CommandWhitelistWaterfall.getAudiences();

        String label = CommandUtil.getCommandLabel(command);
        HashSet<String> commands = CommandWhitelistWaterfall.getCommands(player);
        if (!commands.contains(label)) {
            event.setCancelled(true);
            Component message = CWCommand.getParsedErrorMessage(
                    command,
                    configCache.prefix + CommandWhitelistWaterfall.getCommandDeniedMessage(label)
            );
            audiences.player(player).sendMessage(message);
            return;
        }

        HashSet<String> bannedSubCommands = CommandWhitelistWaterfall.getSuggestions(player);
        for (String bannedSubCommand : bannedSubCommands) {
            if (command.startsWith(bannedSubCommand)) {
                event.setCancelled(true);
                audiences.player(player).sendMessage(CWCommand.miniMessage.deserialize(configCache.prefix + configCache.subcommand_denied));
                return;
            }
        }
    }
}
