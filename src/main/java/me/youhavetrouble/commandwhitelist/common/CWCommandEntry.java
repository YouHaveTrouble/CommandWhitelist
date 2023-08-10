package me.youhavetrouble.commandwhitelist.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CWCommandEntry {

    private final String rawEntry;
    private final List<Pattern> parts;

    protected CWCommandEntry(String command) {
        this.rawEntry = command;
        String[] parts = command.split(" ");
        this.parts = new ArrayList<>(parts.length);
        for (String part : parts) {
            Pattern pattern = Pattern.compile(part);
            this.parts.add(pattern);
        }
    }

    /**
     * Gets the command parts.
     * @return command parts
     */
    public List<Pattern> getParts() {
        return Collections.unmodifiableList(parts);
    }

    /**
     * Gets the raw command entry as given in the constructor.
     * @return raw command entry
     */
    public String getRawEntry() {
        return rawEntry;
    }

    /**
     * Checks if a full command input matches this command entry. Input is a match if all entry slices match the
     * corresponding command slices, even if there are more command slices than entry slices.
     * @param command full command input
     * @return true if the command matches this command entry
     */
    public boolean matches(String command) {
        if (command == null) return false;
        if (command.startsWith("/")) command = command.substring(1); // Remove leading slash (if present)
        String[] parts = command.split(" ");
        if (parts.length < this.parts.size()) return false;
        for (int i = 0; i < this.parts.size(); i++) {
            if (!argumentMatches(parts[i], i)) return false;
        }
        return true;
    }

    /**
     * Checks if a command input partially matches this command entry.
     * @param command command input
     * @return true if the command partially matches this command entry
     */
    public boolean partiallyMatches(String command) {
        if (command == null) return false;
        if (command.startsWith("/")) command = command.substring(1); // Remove leading slash (if present)
        String[] parts = command.split(" ");
        for (int i = 0; i < this.parts.size() || i < parts.length; i++) {
            if (!argumentMatches(parts[i], i)) return false;
        }
        return true;
    }

    /**
     * Checks if a command argument matches this command entry.
     * @param argument command argument
     * @param index argument index
     * @return true if the argument matches this command entry
     */
    public boolean argumentMatches(String argument, int index) {
        if (index < 0 || index >= parts.size()) return false;
        return parts.get(index).matcher(argument).matches();
    }


}
