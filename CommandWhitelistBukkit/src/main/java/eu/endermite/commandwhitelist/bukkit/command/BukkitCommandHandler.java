package eu.endermite.commandwhitelist.bukkit.command;

import eu.endermite.commandwhitelist.common.CWGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BukkitCommandHandler {

    /**
     * @param player Bukkit Player
     * @return commands available to the player
     */
    public static HashSet<String> getCommands(org.bukkit.entity.Player player, HashMap<String, CWGroup> groups) {
        HashSet<String> commandList = new HashSet<>();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue().getCommands());
            else if (player.hasPermission("commandwhitelist.group." + s.getKey()))
                commandList.addAll(s.getValue().getCommands());
        }
        return commandList;
    }

    /**
     * @param player Bukkit Player
     * @return subcommands unavailable for the player
     */
    public static HashSet<String> getSuggestions(org.bukkit.entity.Player player, HashMap<String, CWGroup> groups) {
        HashSet<String> suggestionList = new HashSet<>();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                suggestionList.addAll(s.getValue().getSubCommands());
            if (player.hasPermission("commandwhitelist.group." + s.getKey()))
                continue;
            suggestionList.addAll(s.getValue().getSubCommands());
        }
        return suggestionList;
    }

}
