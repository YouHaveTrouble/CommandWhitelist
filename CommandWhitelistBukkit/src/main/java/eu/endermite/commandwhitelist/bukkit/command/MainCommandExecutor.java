package eu.endermite.commandwhitelist.bukkit.command;

import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.help.HelpTopic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        BukkitAudiences audiences = CommandWhitelistBukkit.getAudiences();

        if (args.length == 0) {
            audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
            return true;
        }
        try {
            CWCommand.CommandType commandType = CWCommand.CommandType.valueOf(args[0].toUpperCase());
            switch (commandType) {
                case RELOAD:
                    if (!sender.hasPermission(CWPermission.RELOAD.permission())) {
                        audiences.sender(sender).sendMessage(CWCommand.miniMessage.parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    CommandWhitelistBukkit.getPlugin().reloadPluginConfig(sender);
                    return true;
                case ADD:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        audiences.sender(sender).sendMessage(CWCommand.miniMessage.parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().added_to_whitelist, args[2], args[1])));
                        else
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/" + label + " add <group> <command>"));
                    return true;
                case REMOVE:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        audiences.sender(sender).sendMessage(CWCommand.miniMessage.parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().removed_from_whitelist, args[2], args[1])));
                        else
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/" + label + " remove <group> <command>"));
                    return true;
                case HELP:
                default:
                    audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
            }
        } catch (IllegalArgumentException e) {
            audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> serverCommands;
        try {
            serverCommands = new ArrayList<>(Bukkit.getCommandMap().getKnownCommands().keySet());
        } catch (NoSuchMethodError error) {
            HashSet<String> commands = new HashSet<>();
            for (HelpTopic topic : Bukkit.getHelpMap().getHelpTopics()) {
                String cmd = topic.getName();
                if (Character.isUpperCase(cmd.charAt(0))) continue;
                commands.add(topic.getName());
            }
            serverCommands = new ArrayList<>(commands);
        }
        return CWCommand.commandSuggestions(CommandWhitelistBukkit.getConfigCache(), serverCommands, args, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission()), CWCommand.ImplementationType.BUKKIT);
    }
}
