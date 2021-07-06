package eu.endermite.commandwhitelist.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.velocity.CommandWhitelistVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

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
                        sender.sendMessage(MiniMessage.markdown().parse(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().no_permission));
                        return;
                    }
                    CommandWhitelistVelocity.reloadConfig(sender);
                    return;
                case ADD:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        sender.sendMessage(MiniMessage.markdown().parse(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(CommandWhitelistVelocity.getConfigCache(), args[2], args[1]))
                            sender.sendMessage(MiniMessage.markdown().parse(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().added_to_whitelist));
                        else
                            sender.sendMessage(MiniMessage.markdown().parse(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().group_doesnt_exist));
                    } else
                        sender.sendMessage(Component.text("/"+label+" add <group> <command>"));
                    return;
                case REMOVE:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        sender.sendMessage(MiniMessage.markdown().parse(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(CommandWhitelistVelocity.getConfigCache(), args[2], args[1]))
                            sender.sendMessage(MiniMessage.markdown().parse(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().removed_from_whitelist));
                        else
                            sender.sendMessage(MiniMessage.markdown().parse(CommandWhitelistVelocity.getConfigCache().prefix + CommandWhitelistVelocity.getConfigCache().group_doesnt_exist));
                    } else
                        sender.sendMessage(Component.text("/"+label+" remove <group> <command>"));
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
            List<String> serverCommands = new ArrayList<>();
            //TODO find out how to get all registered commands

            // This is probably very broken now
            return CWCommand.commandSuggestions(CommandWhitelistVelocity.getConfigCache(), serverCommands, args, source.hasPermission(CWPermission.RELOAD.permission()), source.hasPermission(CWPermission.ADMIN.permission()));
        });
    }
}
