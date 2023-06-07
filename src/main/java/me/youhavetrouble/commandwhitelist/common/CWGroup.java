package me.youhavetrouble.commandwhitelist.common;

import java.util.*;

public class CWGroup {

    private final String id;
    private final HashSet<CWCommandEntry> commands = new HashSet<>();

    public CWGroup(String id, Collection<String> commands) {
        this.id = id;
        for (String command : commands) {
            this.commands.add(new CWCommandEntry(command));
        }
    }

    public String getId() {
        return id;
    }

    public String getPermission() {
        return "commandwhitelist.group." + id;
    }

    public Set<CWCommandEntry> getCommands() {
        return commands;
    }

    public void addCommand(String command) {
        this.commands.add(new CWCommandEntry(command));
    }

    public void removeCommand(String command) {
        commands.removeIf(cwCommandEntry -> cwCommandEntry.getRawEntry().equals(command));
    }

    public HashMap<String, Object> serialize() {
        HashMap<String, Object> serializedGroup = new LinkedHashMap<>();
        List<String> commands = new ArrayList<>();
        for (CWCommandEntry command : this.commands) {
            commands.add(command.getRawEntry());
        }
        serializedGroup.put("commands", commands);
        return serializedGroup;
    }
}
