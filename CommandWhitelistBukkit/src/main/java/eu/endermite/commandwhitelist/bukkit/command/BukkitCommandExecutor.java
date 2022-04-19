package eu.endermite.commandwhitelist.bukkit.command;

import eu.endermite.commandwhitelist.bukkit.CommandWhitelistBukkit;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.File;
import java.util.List;

public class BukkitCommandExecutor implements TabExecutor {

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
                        audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    CommandWhitelistBukkit.getPlugin().reloadPluginConfig(sender);
                    return true;
                case ADD:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().added_to_whitelist, args[2], args[1])));
                        else
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/" + label + " add <group> <command>"));
                    return true;
                case REMOVE:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(CommandWhitelistBukkit.getConfigCache(), args[2], args[1]))
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().removed_from_whitelist, args[2], args[1])));
                        else
                            audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/" + label + " remove <group> <command>"));
                    return true;
                case DUMP:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistBukkit.getConfigCache().prefix + CommandWhitelistBukkit.getConfigCache().no_permission));
                        return true;
                    }
                    audiences.sender(sender).sendMessage(Component.text("Dumping all available commands to a file..."));
                    if (CommandUtil.dumpAllBukkitCommands(CommandWhitelistBukkit.getServerCommands(), new File("plugins/CommandWhitelist/config.yml"))) {
                        audiences.sender(sender).sendMessage(Component.text("Commands dumped to command_dump.yml"));
                    } else {
                        audiences.sender(sender).sendMessage(Component.text("Failed to save the file."));
                    }
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
        return CWCommand.commandSuggestions(
                CommandWhitelistBukkit.getConfigCache(),
                CommandWhitelistBukkit.getServerCommands(),
                args,
                sender.hasPermission(CWPermission.RELOAD.permission()),
                sender.hasPermission(CWPermission.ADMIN.permission()),
                CWCommand.ImplementationType.BUKKIT
        );
    }
}
