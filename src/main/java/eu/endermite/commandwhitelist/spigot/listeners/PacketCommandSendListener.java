package eu.endermite.commandwhitelist.spigot.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import eu.endermite.commandwhitelist.api.CommandsList;
import eu.endermite.commandwhitelist.api.RandomStuff;
import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import eu.endermite.commandwhitelist.spigot.config.ConfigCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.List;
import java.util.Map;

public class PacketCommandSendListener {

    public static void protocol(CommandWhitelist plugin) {
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
                String cmd = string.replace("/", "");
                String[] split = cmd.split("\\s+");
                String command = split[0].toLowerCase();
                for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermList().entrySet()) {
                    if (!player.hasPermission("commandwhitelist.commands." + s.getKey()))
                        continue;
                    for (String comm : s.getValue()) {
                        comm = comm.toLowerCase();
                        if (command.equalsIgnoreCase(comm) || command.startsWith(comm + " ")) {
                            List<String> bannedSubCommands = CommandsList.getSuggestions(player);
                            for (String bannedSubCommand : bannedSubCommands) {
                                if (cmd.startsWith(bannedSubCommand)) {
                                    event.setCancelled(true);
                                    ConfigCache config = CommandWhitelist.getConfigCache();
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getPrefix() + RandomStuff.getMessage(config.getCommandDeniedList(), config.getSubCommandDenied())));
                                    return;
                                }
                            }
                            return;
                        }
                    }
                }

                event.setCancelled(true);
                ConfigCache config = CommandWhitelist.getConfigCache();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getPrefix() + RandomStuff.getMessage(config.getCommandDeniedList(), config.getCommandDenied())));

            }
        });
    }
}
