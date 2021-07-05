package eu.endermite.commandwhitelist.bukkit.command;

import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.help.HelpTopic;

import java.util.ArrayList;
import java.util.List;

public class MainCommandExecutor implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        BukkitAudiences audiences = CommandWhitelistBukkit.getAudiences();

        if (args.length == 0) {
            audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission("commandwhitelist.reload"), sender.hasPermission("commandwhitelist.admin")));
            return true;
        }

        try {
            CWCommand.CommandType commandType = CWCommand.CommandType.valueOf(args[0].toUpperCase());
            switch (commandType) {
                case RELOAD:
                    if (!sender.hasPermission("commandwhitelist.reload")) {
                        audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    CommandWhitelistBukkit.getPlugin().reloadPluginConfig(sender);
                    return true;
                case ADD:
                    if (!sender.hasPermission("commandwhitelist.admin")) {
                        audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().added_to_whitelist, args[2], args[1])));
                        else
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/" + label + " add <group> <command>"));
                    return true;
                case REMOVE:
                    if (!sender.hasPermission("commandwhitelist.admin")) {
                        audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().removed_from_whitelist, args[2], args[1])));
                        else
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/" + label + " remove <group> <command>"));
                    return true;
                case HELP:
                default:
                    audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission("commandwhitelist.reload"), sender.hasPermission("commandwhitelist.admin")));
            }

        } catch (IllegalArgumentException e) {
            audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission("commandwhitelist.reload"), sender.hasPermission("commandwhitelist.admin")));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        ConfigCache config = CommandWhitelistBukkit.getConfigCache();
        if (args.length == 1) {
            if ("reload".startsWith(args[0]) && sender.hasPermission("commandwhitelist.reload")) {
                list.add("reload");
            }
            if ("add".startsWith(args[0]) && sender.hasPermission("commandwhitelist.admin")) {
                list.add("add");
            }
            if ("remove".startsWith(args[0]) && sender.hasPermission("commandwhitelist.admin")) {
                list.add("remove");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("commandwhitelist.admin"))
                    return list;
                for (String s : config.getGroupList().keySet()) {
                    if (s.startsWith(args[1])) {
                        list.add(s);
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("commandwhitelist.admin"))
                    return list;
                try {
                    for (String s : config.getGroupList().get(args[1]).getCommands()) {
                        if (s.startsWith(args[2])) {
                            list.add(s);
                        }
                    }
                } catch (NullPointerException ignored) {}
                return list;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("commandwhitelist.admin"))
                    return list;

                for (HelpTopic s : CommandWhitelistBukkit.getPlugin().getServer().getHelpMap().getHelpTopics()) {
                    String cmd = s.getName();
                    if (!cmd.startsWith("/"))
                        continue;
                    try {
                        if (cmd.contains(":")) {
                            cmd = cmd.split(":")[1];
                        }
                    } catch (Exception e) {
                        continue;
                    }
                    cmd = cmd.replace("/", "");

                    if (config.getGroupList().get(args[1]) == null)
                        continue;

                    if (config.getGroupList().get(args[1]).getCommands().contains(cmd))
                        continue;

                    if (cmd.startsWith(args[2])) {
                        list.add(cmd);
                    }
                }
                return list;
            }
        }
        return list;
    }
}
