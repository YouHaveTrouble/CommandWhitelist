package eu.endermite.commandwhitelist.waterfall.command;

import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.waterfall.CommandWhitelistWaterfall;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
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

        String label = getName();
        ConfigCache configCache = CommandWhitelistWaterfall.getConfigCache();
        BungeeAudiences audiences = CommandWhitelistWaterfall.getAudiences();

        if (args.length == 0) {
            audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
            return;
        }

        try {
            CWCommand.CommandType commandType = CWCommand.CommandType.valueOf(args[0].toUpperCase());
            switch (commandType) {
                case RELOAD:
                    if (!sender.hasPermission(CWPermission.RELOAD.permission())) {
                        audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(CommandWhitelistWaterfall.getConfigCache().prefix + configCache.no_permission));
                        return;
                    }
                    CommandWhitelistWaterfall.getPlugin().loadConfigAsync(sender);
                    return;
                case ADD:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(configCache.prefix + configCache.no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.addToWhitelist(configCache, args[2], args[1]))
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(configCache.prefix + configCache.added_to_whitelist));
                        else
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(configCache.prefix + configCache.group_doesnt_exist));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/"+label+" add <group> <command>"));
                    return;
                case REMOVE:
                    if (!sender.hasPermission(CWPermission.ADMIN.permission())) {
                        audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(configCache.prefix + configCache.no_permission));
                        return;
                    }
                    if (args.length == 3) {
                        if (CWCommand.removeFromWhitelist(configCache, args[2], args[1]))
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(configCache.prefix + configCache.removed_from_whitelist));
                        else
                            audiences.sender(sender).sendMessage(MiniMessage.markdown().parse(configCache.prefix + configCache.group_doesnt_exist));
                    } else
                        audiences.sender(sender).sendMessage(Component.text("/"+label+" remove <group> <command>"));
                    return;
                case HELP:
                default:
                    audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
            }

        } catch (IllegalArgumentException e) {
            audiences.sender(sender).sendMessage(CWCommand.helpComponent(label, sender.hasPermission(CWPermission.RELOAD.permission()), sender.hasPermission(CWPermission.ADMIN.permission())));
        }
        return;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
