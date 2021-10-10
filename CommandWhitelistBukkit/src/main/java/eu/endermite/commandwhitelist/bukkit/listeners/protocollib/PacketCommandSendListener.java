package eu.endermite.commandwhitelist.bukkit.listeners.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.brigadier.tree.RootCommandNode;
import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.CWPermission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;

public class PacketCommandSendListener {

    public static void protocol(CommandWhitelistBukkit plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        commandSendListener(protocolManager, plugin);
    }

    public static void commandSendListener(ProtocolManager protocolManager, Plugin plugin) {
        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.COMMANDS) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                if (player.hasPermission(CWPermission.BYPASS.permission())) return;

                HashSet<String> commandList = CommandWhitelistBukkit.getCommands(player);
                PacketContainer packet = event.getPacket();
                RootCommandNode<?> node = (RootCommandNode<?>) packet.getModifier().getValues().get(0);
                node.getChildren().removeIf((cmd) -> !commandList.contains(cmd.getName()));
            }
        });
    }

}
