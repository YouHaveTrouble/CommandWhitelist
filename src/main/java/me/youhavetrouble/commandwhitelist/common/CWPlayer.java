package me.youhavetrouble.commandwhitelist.common;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents a player on any supported platform.
 */
public class CWPlayer {

    private org.bukkit.entity.Player bukkitPlayer;
    private com.velocitypowered.api.proxy.Player velocityPlayer;
    private net.md_5.bungee.api.connection.ProxiedPlayer waterfallPlayer;

    public CWPlayer(org.bukkit.entity.Player player) {
        this.bukkitPlayer = player;
    }

    public CWPlayer(com.velocitypowered.api.proxy.Player player) {
        this.velocityPlayer = player;
    }

    public CWPlayer(net.md_5.bungee.api.connection.ProxiedPlayer player) {
        this.waterfallPlayer = player;
    }

    /**
     * Checks if this player has a permission.
     * @param permission Permission to check
     * @return Whether this player has the permission
     */
    public boolean hasPermission(String permission) {
        if (bukkitPlayer != null) return bukkitPlayer.hasPermission(permission);
        if (velocityPlayer != null) return velocityPlayer.hasPermission(permission);
        if (waterfallPlayer != null) return waterfallPlayer.hasPermission(permission);
        return false;
    }

    /**
     * Get the commands that this player can use.
     * @param CWConfig Current configuration cache
     * @return Set of commands that this player can use
     */
    public Set<CWCommandEntry> getCommands(CWConfig CWConfig) {
        HashSet<CWCommandEntry> commands = new HashSet<>();
        Map<String, CWGroup> groups =  CWConfig.getGroupList();
        for (Map.Entry<String, CWGroup> groupEntry : groups.entrySet()) {
            CWGroup group = groupEntry.getValue();
            String groupId = groupEntry.getKey();
            if (groupId.equalsIgnoreCase("default")) commands.addAll(group.getCommands());
            else if (hasPermission(group.getPermission())) commands.addAll(group.getCommands());
        }
        return commands;
    }

}
