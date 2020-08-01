package eu.endermite.commandwhitelist.bungee.command;

import eu.endermite.commandwhitelist.bungee.CommandWhitelistBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;


public class BungeeMainCommand extends Command {
    public BungeeMainCommand() {
        super("bungeecommandwhitelist", "none", "bcw");

    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("commandwhitelist.reload")) {
                    CommandWhitelistBungee.getPlugin().loadConfigAsync(sender);
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', CommandWhitelistBungee.getConfigCache().getPrefix() + CommandWhitelistBungee.getConfigCache().getNoPermission()));
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
}
