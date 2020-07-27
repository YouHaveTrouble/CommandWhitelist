package eu.endermite.commandwhitelist.command;

import eu.endermite.commandwhitelist.CommandWhitelist;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("commandwhitelist.reload")) {
                    CommandWhitelist.getPlugin().reloadPluginConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getConfigReloaded()));

                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getNoPermission()));
                }
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelist.getConfigCache().getPrefix() + CommandWhitelist.getConfigCache().getNoSubCommand()));
            }
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCommand Whitelist by YouHaveTrouble"));
            if (sender.hasPermission("commandwhitelist.reload")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9/cw reload &b- Reload plugin configuration"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> list = new ArrayList<>();

        if(args.length == 1) {
            if ("restart".startsWith(args[0]) && sender.hasPermission("commandwhitelist.reload")) {
                list.add("reload");
            }
        }

        return list;
    }
}
