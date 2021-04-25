package eu.endermite.commandwhitelist.waterfall.command;

import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.waterfall.CommandWhitelistWaterfall;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeMainCommand extends Command implements TabExecutor {

    public BungeeMainCommand(String name) {
        super(name);
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            // send help
            return;
        }

        try {
            CWCommand.CommandType commandType = CWCommand.CommandType.valueOf(args[0]);
            switch (commandType) {
                case RELOAD:
                    if (!sender.hasPermission("commandwhitelist.reload")) {
                        CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().no_permission));
                        return;
                    }
                    CommandWhitelistWaterfall.getPlugin().loadConfigAsync(sender);
                    return;
                case ADD:
                    if (!sender.hasPermission("commandwhitelist.admin")) {
                        CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(CommandWhitelistWaterfall.getConfigCache(), args[2], args[1]))
                            CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().added_to_whitelist));
                        else
                            CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().group_doesnt_exist));
                    } else
                        CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(Component.text("/cw add <group> <command>"));
                    return;
                case REMOVE:
                    if (!sender.hasPermission("commandwhitelist.admin")) {
                        CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(CommandWhitelistWaterfall.getConfigCache(), args[2], args[1]))
                            CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().removed_from_whitelist));
                        else
                            CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + CommandWhitelistWaterfall.getConfigCache().group_doesnt_exist));
                    } else
                        CommandWhitelistWaterfall.getAudiences().sender(sender).sendMessage(Component.text("/cw remove <group> <command>"));
                    return;
                case HELP:
                default:
                    // send help
            }

        } catch (IllegalArgumentException e) {
            // send help
        }
        return;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
