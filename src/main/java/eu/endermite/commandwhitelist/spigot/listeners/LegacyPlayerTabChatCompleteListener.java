package eu.endermite.commandwhitelist.spigot.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import eu.endermite.commandwhitelist.api.CommandsList;
import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegacyPlayerTabChatCompleteListener {

    public static void protocol(CommandWhitelist plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        tabCompleteServerBound(protocolManager, plugin);
        tabCompleteClientBound(protocolManager, plugin);
    }

    public static void tabCompleteServerBound(ProtocolManager protocolManager, Plugin plugin) {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.TAB_COMPLETE) {
            @Override
            public void onPacketSending(PacketEvent event) {

                try {
                    Player player = event.getPlayer();
                    if (player.hasPermission("commandwhitelist.bypass")) {
                        return;
                    }
                    PacketContainer packet = event.getPacket();
                    String[] message = packet.getSpecificModifier(String[].class).read(0);

                    List<String> commandList = CommandsList.getCommands(player);

                    List<String> finalList = new ArrayList<>();
                    int components = 0;
                    for (String cmd : message) {
                        for (String cmdFromList : commandList) {
                            if (cmd.equalsIgnoreCase("/" + cmdFromList) || !cmd.startsWith("/")) {
                                finalList.add(components++, cmd);
                                break;
                            }
                        }
                    }

                    String[] toWrite = new String[components];
                    int counter = 0;
                    for (String cmd : finalList) {
                        toWrite[counter++] = cmd;
                    }

                    packet.getSpecificModifier(String[].class).write(0, toWrite);

                } catch (Exception ignored) {}
            }
        });
    }

    public static void tabCompleteClientBound(ProtocolManager protocolManager, Plugin plugin) {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    Player player = event.getPlayer();
                    if (player.hasPermission("commandwhitelist.bypass")) {
                        return;
                    }
                    PacketContainer packet = event.getPacket();
                    String command = packet.getSpecificModifier(String.class).read(0);

                    for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermList().entrySet()) {
                        if (!player.hasPermission("commandwhitelist.commands." + s.getKey()))
                            continue;
                        for (String comm : s.getValue()) {
                            comm = comm.toLowerCase();
                            if (command.equalsIgnoreCase("/" + comm))
                                return;
                            else if (command.startsWith("/" + comm + " ")) {
                                return;
                            }
                        }
                    }
                    event.setCancelled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
