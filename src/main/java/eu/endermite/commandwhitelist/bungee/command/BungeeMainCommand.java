package eu.endermite.commandwhitelist.bungee.command;

import eu.endermite.commandwhitelist.bungee.CommandWhitelistBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BungeeMainCommand extends Command implements TabExecutor {

    public BungeeMainCommand(String name) {
        super(name);
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("commandwhitelist.reload")) {
                    CommandWhitelistBungee.getPlugin().loadConfigAsync(sender);
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelistBungee.getConfigCache().getPrefix() + CommandWhitelistBungee.getConfigCache().getNoPermission()));
                }
            } else if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("commandwhitelist.admin")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelistBungee.getConfigCache().getPrefix() + CommandWhitelistBungee.getConfigCache().getNoPermission()));
                    return;
                }
                if (args.length >= 3) {
                    if (CommandWhitelistBungee.getConfigCache().addCommand(args[2], args[1])) {
                        String msg = String.format(CommandWhitelistBungee.getConfigCache().getWhitelistedCommand(), args[2], args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    } else {
                        String msg = CommandWhitelistBungee.getConfigCache().getNoSuchGroup();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }
                } else {
                    sender.sendMessage("/bcw add <group> <command>");
                }
                return;
            } else if (args[0].equalsIgnoreCase("remove")) {

                if (!sender.hasPermission("commandwhitelist.admin")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelistBungee.getConfigCache().getPrefix() + CommandWhitelistBungee.getConfigCache().getNoPermission()));
                    return;
                }
                if (args.length >= 3) {
                    if (CommandWhitelistBungee.getConfigCache().removeCommand(args[2], args[1])) {
                        String msg = String.format(CommandWhitelistBungee.getConfigCache().getRemovedWhitelistedCommand(), args[2], args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    } else {
                        String msg = CommandWhitelistBungee.getConfigCache().getNoSuchGroup();
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }
                } else {
                    sender.sendMessage("/bcw remove <group> <command>");
                }

            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelistBungee.getConfigCache().getPrefix() + CommandWhitelistBungee.getConfigCache().getNoSubCommand()));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCommand Whitelist by YouHaveTrouble"));
            if (sender.hasPermission("commandwhitelist.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/bcw reload &b- Reload bungee plugin configuration"));
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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
                for (Map.Entry<String, List<String>> s : CommandWhitelistBungee.getConfigCache().getPermList().entrySet()) {
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
                    for (String s : CommandWhitelistBungee.getConfigCache().getPermList().get(args[1])) {
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

                for (Map.Entry<String, Command> command : CommandWhitelistBungee.getPlugin().getProxy().getPluginManager().getCommands()) {
                    if (command.getKey().startsWith("/"))
                        continue;

                    if (CommandWhitelistBungee.getConfigCache().getPermList().get(args[1]).contains(command.getKey()))
                        continue;

                    if (command.getKey().startsWith(args[2]))
                        list.add(command.getKey());
                }
            }
        }
        return list;
    }
}
