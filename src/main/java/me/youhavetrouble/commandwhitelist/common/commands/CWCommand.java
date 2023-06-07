package me.youhavetrouble.commandwhitelist.common.commands;

import me.youhavetrouble.commandwhitelist.common.CWCommandEntry;
import me.youhavetrouble.commandwhitelist.common.CWGroup;
import me.youhavetrouble.commandwhitelist.common.ConfigCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CWCommand {

    public static MiniMessage miniMessage = MiniMessage.miniMessage();

    public static boolean addToWhitelist(ConfigCache configCache, String command, String group) {
        CWGroup cwGroup = configCache.getGroupList().get(group);
        if (cwGroup == null) return false;
        cwGroup.addCommand(command);
        configCache.saveCWGroup(group, cwGroup);
        return true;
    }

    public static boolean removeFromWhitelist(ConfigCache configCache, String command, String group) {
        CWGroup cwGroup = configCache.getGroupList().get(group);
        if (cwGroup == null)
            return false;
        cwGroup.removeCommand(command);
        configCache.saveCWGroup(group, cwGroup);
        return true;
    }

    public static Component helpComponent(String baseCommand, boolean showReloadCommand, boolean showAdminCommands) {
        final TextComponent.Builder builder = Component.text();
        builder.append(miniMessage.deserialize("<rainbow><bold>CommandWhitelist by YouHaveTrouble</bold>"), Component.newline());
        builder.append(Component.text(baseCommand + " help", NamedTextColor.AQUA), Component.text(" - Displays this message", NamedTextColor.BLUE));
        if (showReloadCommand) {
            builder.append(Component.newline());
            builder.append(Component.text(baseCommand + " reload", NamedTextColor.AQUA), Component.text(" - Reloads plugin configuration", NamedTextColor.BLUE));
        }
        if (showAdminCommands) {
            builder.append(Component.newline());
            builder.append(Component.text(baseCommand + " add <group> <command>", NamedTextColor.AQUA), Component.text(" - Add a command to selected permission group", NamedTextColor.BLUE));
            builder.append(Component.newline());
            builder.append(Component.text(baseCommand + " remove <group> <command>", NamedTextColor.AQUA), Component.text(" - Removes a command from selected permission group", NamedTextColor.BLUE));
        }
        return builder.build();
    }

    public enum CommandType {
        ADD, REMOVE, HELP, RELOAD, DUMP
    }

    public enum ImplementationType {
        BUKKIT, WATERFALL, VELOCITY
    }

    public static List<String> commandSuggestions(
            ConfigCache config,
            Collection<String> serverCommands,
            String[] args, boolean reloadPerm,
            boolean adminPerm,
            ImplementationType implementationType
    ) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 0:
                // thanks velocity for handling completions entirely different from everything else
                if (implementationType.equals(ImplementationType.VELOCITY)) {
                    list.add("help");
                    if (reloadPerm)
                        list.add("reload");
                    if (adminPerm) {
                        list.add("add");
                        list.add("remove");
                        list.add("dump");
                    }
                }
                return list;
            case 1:
                if ("help".startsWith(args[0]))
                    list.add("help");
                if ("reload".startsWith(args[0]) && reloadPerm)
                    list.add("reload");
                if ("add".startsWith(args[0]) && adminPerm)
                    list.add("add");
                if ("remove".startsWith(args[0]) && adminPerm)
                    list.add("remove");
                if ("dump".startsWith(args[0]) && adminPerm)
                    list.add("dump");
                return list;
            case 2:
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                    if (!adminPerm) return list;
                    for (String s : config.getGroupList().keySet()) {
                        if (s.startsWith(args[1]))
                            list.add(s);
                    }
                }
                return list;
            case 3:
                if (args[0].equalsIgnoreCase("remove")) {
                    if (!adminPerm) return list;
                    CWGroup group = config.getGroupList().get(args[1]);
                    if (group == null) return list;
                    // TODO
                    return list;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (!adminPerm) return list;
                    CWGroup group = config.getGroupList().get(args[1]);
                    if (group == null) return list;
                    for (String cmd : serverCommands) {
                        if (cmd.startsWith("/"))
                            cmd = cmd.substring(1);
                        if (cmd.contains(":")) {
                            String[] cmdSplit = cmd.split(":");
                            if (cmdSplit.length < 2) continue;
                            cmd = cmdSplit[1];
                        }
                        if (group.getCommands().contains(cmd)) continue;
                        if (cmd.startsWith(args[2]))
                            list.add(cmd);
                    }
                    return list;
                }
            default:
                return list;
        }
    }

}
