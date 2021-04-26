package eu.endermite.commandwhitelist.common.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.ConfigCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;


public class CWCommand {

    public static boolean addToWhitelist(ConfigCache configCache, String command, String group) {
        CWGroup cwGroup = configCache.getGroupList().get(group);
        if (cwGroup == null)
            return false;
        cwGroup.addCommand(command);
        configCache.reloadConfig();
        return true;
    }

    public static boolean removeFromWhitelist(ConfigCache configCache, String command, String group) {
        CWGroup cwGroup = configCache.getGroupList().get(group);
        if (cwGroup == null)
            return false;
        cwGroup.removeCommand(command);
        configCache.reloadConfig();
        return true;
    }

    public static Component helpComponent(String baseCommand, boolean showReloadCommand, boolean showAdminCommands) {
        Component component = MiniMessage.markdown().parse("<rainbow><bold>CommandWhitelist by YouHaveTrouble")
                .append(Component.newline());
        component = component.append(Component.text("Hover over the command to see what it does!").color(NamedTextColor.AQUA)).decoration(TextDecoration.BOLD, false).append(Component.newline());
        component = component.append(Component.text("/"+baseCommand+" help").color(NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(Component.text("Displays this message"))));
        if (showReloadCommand) {
            component = component.append(Component.newline());
            component = component.append(Component.text("/"+baseCommand+" reload").color(NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(Component.text("Reloads plugin configuration"))));
        }
        if (showAdminCommands) {
            component = component.append(Component.newline());
            component = component.append(Component.text("/"+baseCommand+" add <group> <command>").color(NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(Component.text("Add a command to selected permission group"))));
            component = component.append(Component.newline());
            component = component.append(Component.text("/"+baseCommand+" remove <group> <command>").color(NamedTextColor.AQUA).hoverEvent(HoverEvent.showText(Component.text("Removes a command from selected permission group"))));
        }
        return component;
    }

    public enum CommandType {
        ADD, REMOVE, HELP, RELOAD
    }

}
