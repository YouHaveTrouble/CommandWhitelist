package eu.endermite.commandwhitelist.bukkit.listeners.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;

public class PacketCommandPreProcessListener {

    public static void protocol(CommandWhitelistBukkit plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        commandExecListener(protocolManager, plugin);
    }

    public static void commandExecListener(ProtocolManager protocolManager, Plugin plugin) {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                String string = packet.getStrings().read(0);
                if (!string.startsWith("/")) return;
                Player player = event.getPlayer();
                if (player.hasPermission(CWPermission.BYPASS.permission())) return;
                ConfigCache config = CommandWhitelistBukkit.getConfigCache();
                String caseSensitiveLabel = CommandUtil.getCommandLabel(string);
                String label = caseSensitiveLabel.toLowerCase();
                packet.getStrings().write(0, string.replaceFirst(caseSensitiveLabel, label));

                HashSet<String> commands = CommandWhitelistBukkit.getCommands(player);
                BukkitAudiences audiences = CommandWhitelistBukkit.getAudiences();
                if (!commands.contains(label)) {
                    event.setCancelled(true);
                    audiences.player(player).sendMessage(CWCommand.miniMessage.deserialize(config.prefix + CommandWhitelistBukkit.getCommandDeniedMessage(label)));
                    return;
                }
                HashSet<String> bannedSubCommands = CommandWhitelistBukkit.getSuggestions(player);
                for (String bannedSubCommand : bannedSubCommands) {
                    if (string.toLowerCase().substring(1).startsWith(bannedSubCommand)) {
                        event.setCancelled(true);
                        CommandWhitelistBukkit.getAudiences().player(player).sendMessage(CWCommand.miniMessage.deserialize(config.prefix + config.subcommand_denied));
                        return;
                    }
                }
            }
        });
    }
}
