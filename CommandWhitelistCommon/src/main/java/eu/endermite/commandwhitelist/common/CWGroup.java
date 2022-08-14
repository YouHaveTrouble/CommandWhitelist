package eu.endermite.commandwhitelist.common;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CWGroup {

    private final String id, permission, commandDeniedMessage;
    private final HashSet<String> commandSuggestions = new HashSet<>();
    private final HashSet<String> commands = new HashSet<>();
    private final HashSet<String> subCommands = new HashSet<>();

    public CWGroup(String id, Collection<String> commandSuggestions, Collection<String> commands, Collection<String> subCommands, String custom_command_denied_message) {
        this.id = id;
        this.permission = "commandwhitelist.group." + id;
        this.commandSuggestions.addAll(commandSuggestions);
        this.commands.addAll(commands);
        this.commandDeniedMessage = custom_command_denied_message;
        this.subCommands.addAll(subCommands);
    }

    public String getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public HashSet<String> getCommandSuggestions() {
        return commandSuggestions;
    }

    public HashSet<String> getCommands() {
        return commands;
    }

    public @Nullable String getCommandDeniedMessage() {
        return commandDeniedMessage;
    }

    public void addCommand(String command) {
        if (command.startsWith("~")) {
            commands.add(command.substring(1));
        } else {
            commandSuggestions.add(command);
            commands.add(command);
        }
    }

    public void removeCommand(String command) {
        commandSuggestions.remove(command);
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
        List<String> commands = commandSuggestions.stream()
                .map((suggestion) -> this.commands.contains(suggestion) ? suggestion : "~".concat(suggestion))
                .collect(Collectors.toList());
        List<String> subCommands = new ArrayList<>(this.subCommands);
        serializedGroup.put("commands", commands);
        serializedGroup.put("subcommands", subCommands);
        return serializedGroup;
    }
}
