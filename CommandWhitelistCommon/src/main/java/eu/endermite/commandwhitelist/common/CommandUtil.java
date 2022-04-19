package eu.endermite.commandwhitelist.common;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandUtil {

    /**
     * Filters blocked command suggestions from provided collection of strings
     *
     * @param buffer             Command buffer
     * @param suggestions        Full suggestions list
     * @param blockedSubCommands Subcommands to filter out
     * @return Filtered list of suggestions
     */
    public static List<String> filterSuggestions(String buffer, Collection<String> suggestions, Collection<String> blockedSubCommands) {
        if (buffer.startsWith("/"))
            buffer = buffer.substring(1);
        List<String> suggestionsList = new ArrayList<>(suggestions);
        if (suggestions.isEmpty() || blockedSubCommands.isEmpty()) return suggestionsList;
        for (String s : blockedSubCommands) {
            String scommand = cutLastArgument(s);
            if (buffer.startsWith(scommand)) {
                String slast = getLastArgument(s);
                while (suggestionsList.contains(slast))
                    suggestionsList.remove(slast);
            }
        }
        return suggestionsList;
    }

    /**
     * @param cmd The command
     * @return Last argument of the command
     */
    public static String getLastArgument(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts.length == 0) return "";
        return parts[parts.length - 1];
    }

    /**
     * @param cmd The command
     * @return Command without the last argument.
     */
    public static String cutLastArgument(String cmd) {
        String[] cmdSplit = cmd.split(" ");
        StringBuilder cmdBuilder = new StringBuilder();
        for (int i = 0; i <= cmdSplit.length - 2; i++)
            cmdBuilder.append(cmdSplit[i]).append(" ");
        return cmdBuilder.toString();
    }

    /**
     * @param cmd The command
     * @return Command label
     */
    public static String getCommandLabel(String cmd) {
        String[] parts = cmd.split(" ");
        if (parts[0].startsWith("/"))
            parts[0] = parts[0].substring(1);
        return parts[0];
    }

    /**
     * Dumps command list to a file
     *
     * @param serverCommands Commands to dump
     * @return True on successful file save
     */
    public static boolean dumpAllBukkitCommands(ArrayList<String> serverCommands, File file) {
        try {
            File parent = new File(file.getParent());
            if (!parent.exists())
                parent.mkdir();
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            return false;
        }

        ConfigFile dumpFile = ConfigFile.loadConfig(file);
        dumpFile.set("commands", serverCommands);
        try {
            dumpFile.save();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
