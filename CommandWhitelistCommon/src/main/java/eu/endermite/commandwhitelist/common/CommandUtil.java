package eu.endermite.commandwhitelist.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandUtil {

    /**
     * Filters blocked command suggestions from provided collection of strings
     * @param buffer Command buffer
     * @param suggestions Full suggestions list
     * @param blockedSubCommands Subcommands to filter out
     * @return Filtered list of suggestions
     */
    public static List<String> filterSuggestions(String buffer, Collection<String> suggestions, Collection<String> blockedSubCommands) {
        String cmd = buffer.replace(getLastArgument(buffer), "");
        for (String s : blockedSubCommands) {
            String slast = getLastArgument(s.toLowerCase());
            String scommand = s.replace(slast, "");
            cmd = cmd.replace(getLastArgument(cmd), "");
            if (cmd.substring(1).startsWith("/" + scommand)) {
                while (suggestions.contains(slast))
                    suggestions.remove(slast);
            }
        }
        return new ArrayList<>(suggestions);
    }

    /**
     * @param cmd The command
     * @return Last argument of the command
     */
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

}
