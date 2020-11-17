package eu.endermite.commandwhitelist.spigot.command;

import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.help.HelpTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("commandwhitelist.reload")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getNoPermission()));
                    return true;
                }
                CommandWhitelist.getPlugin().reloadPluginConfig(sender);

            } else if (args[0].equalsIgnoreCase("add")) {

                if (!sender.hasPermission("commandwhitelist.admin")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getNoPermission()));
                    return true;
                }
                if (args.length >= 3) {
                    if (CommandWhitelist.getConfigCache().addCommand(args[2], args[1])) {
                        String msg = String.format(CommandWhitelist.getConfigCache().getWhitelistedCommand(), args[2], args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    } else {
                        String msg = CommandWhitelist.getConfigCache().getNoSuchGroup();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }
                } else {
                    sender.sendMessage("/cw add <group> <command>");
                }

            } else if (args[0].equalsIgnoreCase("remove")) {

                if (!sender.hasPermission("commandwhitelist.admin")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getNoPermission()));
                    return true;
                }
                if (args.length >= 3) {
                    if (CommandWhitelist.getConfigCache().removeCommand(args[2], args[1])) {
                        String msg = String.format(CommandWhitelist.getConfigCache().getRemovedWhitelistedCommand(), args[2], args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    } else {
                        String msg = CommandWhitelist.getConfigCache().getNoSuchGroup();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }
                } else {
                    sender.sendMessage("/cw remove <group> <command>");
                }

            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getNoSubCommand()));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCommand Whitelist by YouHaveTrouble"));
            if (sender.hasPermission("commandwhitelist.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/cw reload &b- Reload plugin configuration"));
            }
            if (sender.hasPermission("commandwhitelist.admin")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/cw add <group> <command> &b- Add command to group"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/cw remove <group> <command> &b- Remove command from a group"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
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
                for (Map.Entry<String, List<String>> s : CommandWhitelist.getConfigCache().getPermList().entrySet()) {
                    if (s.getKey().startsWith(args[1])) {
                        list.add(s.getKey());
                    }
                }
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("commandwhitelist.admin"))
                    return list;
                try {
                    for (String s : CommandWhitelist.getConfigCache().getPermList().get(args[1])) {
                        if (s.startsWith(args[2])) {
                            list.add(s);
                        }
                    }
                } catch (NullPointerException ignored) {
                }
                return list;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("commandwhitelist.admin"))
                    return list;

                for (HelpTopic s : CommandWhitelist.getPlugin().getServer().getHelpMap().getHelpTopics()) {
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

                    if (CommandWhitelist.getConfigCache().getPermList().get(args[1]).contains(cmd))
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
