package eu.endermite.commandwhitelist.bukkit.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
                if (!string.startsWith("/"))
                    return;
                Player player = event.getPlayer();
                if (player.hasPermission("commandwhitelist.bypass"))
                    return;

                ConfigCache configCache = CommandWhitelistBukkit.getConfigCache();

                String label = CommandUtil.getCommandLabel(string.toLowerCase());
                HashSet<String> commands = CommandWhitelistBukkit.getCommands(player, configCache.getGroupList());
                if (!commands.contains(label)) {
                    event.setCancelled(true);
                    ConfigCache config = CommandWhitelistBukkit.getConfigCache();
                    CommandWhitelistBukkit.getAudiences().player(player).sendMessage(MiniMessage.markdown().parse(config.prefix + config.command_denied));
                    return;
                }

                HashSet<String> bannedSubCommands = CommandWhitelistBukkit.getSuggestions(player, configCache.getGroupList());
                for (String bannedSubCommand : bannedSubCommands) {
                    if (string.toLowerCase().substring(1).startsWith(bannedSubCommand)) {
                        event.setCancelled(true);
                        ConfigCache config = CommandWhitelistBukkit.getConfigCache();
                        CommandWhitelistBukkit.getAudiences().player(player).sendMessage(MiniMessage.markdown().parse(config.prefix + config.subcommand_denied));
                        return;
                    }
                }
            }
        });
    }
}
