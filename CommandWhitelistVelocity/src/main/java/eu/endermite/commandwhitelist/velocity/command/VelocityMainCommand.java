package eu.endermite.commandwhitelist.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.velocity.CommandWhitelistVelocity;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VelocityMainCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();
        String label = invocation.alias();

        if (args.length == 0) {
            sender.sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
            return;
        }

        try {
            CWCommand.CommandType commandType = CWCommand.CommandType.valueOf(args[0].toUpperCase());
            switch (commandType) {
                case RELOAD:
                    if (!sender.hasPermission(CWPermission.RELOAD.permission())) {
                        sender.sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().no_permission));
                        return;
                    }
                    CommandWhitelistVelocity.reloadConfig(sender);
                    return;
                case ADD:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        sender.sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(CommandWhitelistVelocity.getConfigCache(), args[2], args[1]))
                            sender.sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().added_to_whitelist, args[2], args[1])));
                        else
                            sender.sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        sender.sendMessage(Component.text("/" + label + " add <group> <command>"));
                    return;
                case REMOVE:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        sender.sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(CommandWhitelistVelocity.getConfigCache(), args[2], args[1]))
                            sender.sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().removed_from_whitelist, args[2], args[1])));
                        else
                            sender.sendMessage(CWCommand.miniMessage.deserialize(String.format(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().group_doesnt_exist, args[1])));
                    } else
                        sender.sendMessage(Component.text("/" + label + " remove <group> <command>"));
                    return;
                case DUMP:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        sender.sendMessage(CWCommand.miniMessage.deserialize(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().no_permission));
                        return;
                    }
                    sender.sendMessage(Component.text("Dumping all available commands to a file..."));
                    if (CommandUtil.dumpAllBukkitCommands(CommandWhitelistVelocity.getServerCommands(), new File(String.valueOf(CommandWhitelistVelocity.getConfigPath()), "command_dump.yml"))) {
                        sender.sendMessage(Component.text("Commands dumped to command_dump.yml"));
                    } else {
                       sender.sendMessage(Component.text("Failed to save the file."));
                    }
                    return;
                case HELP:
                default:
                    sender.sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
            }

        } catch (IllegalArgumentException e) {
            sender.sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
        }
        return;
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        return CompletableFuture.supplyAsync(() -> {
            List<String> serverCommands = CommandWhitelistVelocity.getServerCommands();
            return CWCommand.commandSuggestions(
                    CommandWhitelistVelocity.getConfigCache(),
                    serverCommands,
                    args,
                    source.hasPermission(CWPermission.RELOAD.permission()),
                    source.hasPermission(CWPermission.ADMIN.permission()),
                    CWCommand.ImplementationType.VELOCITY
            );
        });
    }
}
