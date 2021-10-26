package eu.endermite.commandwhitelist.common;

import java.util.*;

public class CWGroup {

    private final String id, permission, custom_command_denied_message;
    private final HashSet<String> commands = new HashSet<>();
    private final HashSet<String> subCommands = new HashSet<>();

    public CWGroup(String id, Collection<String> commands, Collection<String> subCommands, String custom_command_denied_message) {
        this.id = id;
        this.permission = "commandwhitelist.group." + id;
        this.commands.addAll(commands);
        this.custom_command_denied_message = custom_command_denied_message;
        this.subCommands.addAll(subCommands);
    }

    public String getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public HashSet<String> getCommands() {
        return commands;
    }

    public String getCustomCommandDeniedMessage() {
        return custom_command_denied_message;
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void removeCommand(String command) {
        commands.remove(command);
    }

    public HashSet<String> getSubCommands() {
        return subCommands;
    }

    public void addSubCommand(String subCommand) {
        subCommands.add(subCommand);
    }

    public void removeSubCommand(String subCommand) {
        subCommands.remove(subCommand);
    }

    public HashMap<String, Object> serialize() {
        HashMap<String, Object> serializedGroup = new LinkedHashMap<>();
        List<String> commands = new ArrayList<>(this.commands);
        List<String> subCommands = new ArrayList<>(this.subCommands);
        serializedGroup.put("commands", commands);
        serializedGroup.put("subcommands", subCommands);
        return serializedGroup;
    }
}
