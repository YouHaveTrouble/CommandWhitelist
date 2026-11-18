package eu.endermite.commandwhitelist.common;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CWGroup {

    private final String id, permission, commandDeniedMessage;
    private final Set<String> commands;
    private final Set<String> subCommands;

    public CWGroup(String id, Collection<String> commands, Collection<String> subCommands, String custom_command_denied_message) {
        this.id = id;
        this.permission = "commandwhitelist.group." + id;
        this.commands = Collections.unmodifiableSet(new LinkedHashSet<>(commands));
        this.commandDeniedMessage = custom_command_denied_message;
        this.subCommands = Collections.unmodifiableSet(new LinkedHashSet<>(subCommands));
    }

    public String getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public Set<String> getCommands() {
        return commands;
    }

    public @Nullable String getCommandDeniedMessage() {
        return commandDeniedMessage;
    }

    public CWGroup withCommandAdded(String command) {
        Set<String> updatedCommands = new LinkedHashSet<>(commands);
        updatedCommands.add(command);
        return new CWGroup(id, updatedCommands, subCommands, commandDeniedMessage);
    }

    public CWGroup withCommandRemoved(String command) {
        Set<String> updatedCommands = new LinkedHashSet<>(commands);
        updatedCommands.remove(command);
        return new CWGroup(id, updatedCommands, subCommands, commandDeniedMessage);
    }

    public Set<String> getSubCommands() {
        return subCommands;
    }

    public HashMap<String, Object> serialize() {
        HashMap<String, Object> serializedGroup = new LinkedHashMap<>();
        serializedGroup.put("commands", new ArrayList<>(commands));
        serializedGroup.put("subcommands", new ArrayList<>(subCommands));
        return serializedGroup;
    }
}
