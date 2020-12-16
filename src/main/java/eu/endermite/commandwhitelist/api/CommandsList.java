package eu.endermite.commandwhitelist.api;

import eu.endermite.commandwhitelist.bungee.CommandWhitelistBungee;
import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import eu.endermite.commandwhitelist.velocity.CommandWhitelistVelocity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandsList {

    public static List<String> getCommands(org.bukkit.entity.Player player) {
        List<String> commandList = new ArrayList<>();
        for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermList().entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue());
            else if (player.hasPermission("commandwhitelist.commands." + s.getKey()))
                    commandList.addAll(s.getValue());
        }
        return commandList;
    }

    public static List<String> getCommands(net.md_5.bungee.api.connection.ProxiedPlayer player) {
        List<String> commandList = new ArrayList<>();
        for (Map.Entry<String, List<String>> s : CommandWhitelistBungee.getConfigCache().getPermList().entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue());
            else if (player.hasPermission("commandwhitelist.commands." + s.getKey()))
                commandList.addAll(s.getValue());
        }
        return commandList;
    }

    public static List<String> getCommands(com.velocitypowered.api.proxy.Player player) {
        List<String> commandList = new ArrayList<>();
        for (Map.Entry<String, List<String>> s : CommandWhitelistVelocity.getConfigCache().getPermList().entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue());
            else if (player.hasPermission("commandwhitelist.commands." + s.getKey()))
                commandList.addAll(s.getValue());
        }
        return commandList;
    }

    public static List<String> getSuggestions(org.bukkit.entity.Player player) {
        List<String> suggestionList = new ArrayList<>();
        for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermSubList().entrySet()) {
            if (player.hasPermission("commandwhitelist.subcommands." + s.getKey()))
                continue;
            suggestionList.addAll(s.getValue());
        }
        return suggestionList;
    }

    public static String getLastArgument(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length <= 1)
            return "";
        String last = "";
        for (String part : parts) {
            last = part;
        }
        return last;
    }

}
