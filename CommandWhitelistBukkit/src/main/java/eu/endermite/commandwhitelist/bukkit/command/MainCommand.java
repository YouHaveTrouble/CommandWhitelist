package eu.endermite.commandwhitelist.bukkit.command;

import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // send help
            return true;
        }

        try {
            CWCommand.CommandType commandType = CWCommand.CommandType.valueOf(args[0]);
            switch (commandType) {
                case RELOAD:
                    if (!sender.hasPermission("commandwhitelist.reload")) {
                        CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    CommandWhitelistBukkit.getPlugin().reloadPluginConfig(sender);
                    return true;
                case ADD:
                    if (!sender.hasPermission("commandwhitelist.admin")) {
                        CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().added_to_whitelist));
                        else
                            CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist));
                    } else
                        CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(Component.text("/cw add <group> <command>"));
                    return true;
                case REMOVE:
                    if (!sender.hasPermission("commandwhitelist.admin")) {
                        CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().removed_from_whitelist));
                        else
                            CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist));
                    } else
                        CommandWhitelistBukkit.getAudiences().sender(sender).sendMessage(Component.text("/cw remove <group> <command>"));
                    return true;
                case HELP:
                default:
                    // send help
            }

        } catch (IllegalArgumentException e) {
            // send help
        }
        return true;
    }
}
