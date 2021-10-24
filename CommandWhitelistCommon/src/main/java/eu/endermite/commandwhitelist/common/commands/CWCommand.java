package eu.endermite.commandwhitelist.common.commands;

import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.ConfigCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CWCommand {

    public static MiniMessage miniMessage = MiniMessage.builder()
            .removeDefaultTransformations()
            .transformation(TransformationType.COLOR)
            .transformation(TransformationType.DECORATION)
            .transformation(TransformationType.GRADIENT)
            .transformation(TransformationType.RESET)
            .transformation(TransformationType.RAINBOW)
            .transformation(TransformationType.PRE)
            .build();

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
        Component component = miniMessage.parse("<rainbow><bold>CommandWhitelist by YouHaveTrouble")
                .append(Component.newline());
        component = component.append(Component.text("/" + baseCommand + " help").color(NamedTextColor.AQUA).append(Component.text(" - Displays this message").color(NamedTextColor.BLUE)));
        if (showReloadCommand) {
            component = component.append(Component.newline());
            component = component.append(Component.text("/" + baseCommand + " reload").color(NamedTextColor.AQUA).append(Component.text(" - Reloads plugin configuration").color(NamedTextColor.BLUE)));
        }
        if (showAdminCommands) {
            component = component.append(Component.newline());
            component = component.append(Component.text("/" + baseCommand + " add <group> <command>").color(NamedTextColor.AQUA).append(Component.text(" - Add a command to selected permission group").color(NamedTextColor.BLUE)));
            component = component.append(Component.newline());
            component = component.append(Component.text("/" + baseCommand + " remove <group> <command>").color(NamedTextColor.AQUA).append(Component.text(" - Removes a command from selected permission group").color(NamedTextColor.BLUE)));
        }
        return component;
    }

    public enum CommandType {
        ADD, REMOVE, HELP, RELOAD
    }

    public static List<String> commandSuggestions(ConfigCache config, Collection<String> serverCommands, String[] args, boolean reloadPerm, boolean adminPerm) {
        List<String> list = new ArrayList<>();
        switch (args.length) {
            case 0:
            case 1:
                if ("reload".startsWith(args[0]) && reloadPerm)
                    list.add("reload");
                if ("add".startsWith(args[0]) && adminPerm)
                    list.add("add");
                if ("remove".startsWith(args[0]) && adminPerm)
                    list.add("remove");
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
                    for (String s : group.getCommands()) {
                        if (s.startsWith(args[2]))
                            list.add(s);
                    }
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
