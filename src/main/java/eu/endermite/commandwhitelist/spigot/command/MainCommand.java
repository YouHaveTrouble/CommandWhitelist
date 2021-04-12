package eu.endermite.commandwhitelist.spigot.command;

import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("commandwhitelist.reload")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().prefix + CommandWhitelist.getConfigCache().no_permission));
                    return true;
                }
                CommandWhitelist.getPlugin().reloadPluginConfig(sender);

            } else if (args[0].equalsIgnoreCase("add")) {

                if (!sender.hasPermission("commandwhitelist.admin")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().prefix + CommandWhitelist.getConfigCache().no_permission));
                    return true;
                }
                if (args.length >= 3) {

                } else {
                    sender.sendMessage("/cw add <group> <command>");
                }

            } else if (args[0].equalsIgnoreCase("remove")) {

                if (!sender.hasPermission("commandwhitelist.admin")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().prefix + CommandWhitelist.getConfigCache().no_permission));
                    return true;
                }
                if (args.length >= 3) {

                } else {
                    sender.sendMessage("/cw remove <group> <command>");
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().prefix + CommandWhitelist.getConfigCache().no_such_subcommand));
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


}
