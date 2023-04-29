package eu.endermite.commandwhitelist.velocity.command;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.CWPermission;
import eu.endermite.commandwhitelist.common.CommandUtil;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import eu.endermite.commandwhitelist.velocity.CommandWhitelistVelocity;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;

public final class VelocityMainCommand {
    @Inject
    private CommandManager commandManager;
    @Inject
    private CommandWhitelistVelocity plugin;
    @Inject
    @DataDirectory
    private Path dataDirectory;

    public void register() {
        LiteralCommandNode<CommandSource> node = LiteralArgumentBuilder
                .<CommandSource>literal("vcw")
                .requires(src -> src.getPermissionValue("commandwhitelist.command") != Tristate.FALSE)
                .executes(ctx -> {
                    CommandSource source = ctx.getSource();
                    source.sendMessage(CWCommand.helpComponent("vcw", source.hasPermission(CWPermission.RELOAD.permission()), source.hasPermission(CWPermission.ADMIN.permission())));
                    return Command.SINGLE_SUCCESS;
                })
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                    .requires(src -> src.hasPermission(CWPermission.RELOAD.permission()))
                    .executes(ctx -> plugin.reloadConfig(ctx.getSource()))
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("add")
                    .requires(src -> src.hasPermission(CWPermission.ADMIN.permission()))
                    .then(RequiredArgumentBuilder.<CommandSource, String>argument("group", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            plugin.getConfigCache().getGroupList().keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("command", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                CWGroup group = plugin.getConfigCache().getGroupList().get(ctx.getArgument("group", String.class));
                                if (group == null) return builder.buildFuture();

                                for (String cmd : plugin.getServerCommands()) {
                                    if (cmd.charAt(0) == '/')
                                        cmd = cmd.substring(1);
                                    if (cmd.indexOf(':') != -1) {
                                        String[] cmdSplit = cmd.split(":");
                                        if (cmdSplit.length < 2) continue;
                                        cmd = cmdSplit[1];
                                    }
                                    if (group.getCommands().contains(cmd)) continue;
                                    builder.suggest(cmd);
                                }
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                CommandSource source = ctx.getSource();
                                ConfigCache configCache = plugin.getConfigCache();
                                String arg1 = ctx.getArgument("group", String.class);
                                String arg2 = ctx.getArgument("command", String.class);

                                if (CWCommand.addToWhitelist(configCache, arg2, arg1))
                                    source.sendMessage(CWCommand.miniMessage.deserialize(String.format(configCache.prefix + configCache.added_to_whitelist, arg2, arg1)));
                                else
                                    source.sendMessage(CWCommand.miniMessage.deserialize(String.format(configCache.prefix + configCache.group_doesnt_exist, arg1)));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("remove")
                    .requires(src -> src.hasPermission(CWPermission.ADMIN.permission()))
                    .then(RequiredArgumentBuilder.<CommandSource, String>argument("group", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            plugin.getConfigCache().getGroupList().keySet().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("command", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                CWGroup group = plugin.getConfigCache().getGroupList().get(ctx.getArgument("group", String.class));
                                if (group == null) return builder.buildFuture();

                                for (String s : group.getCommands()) {
                                    builder.suggest(s);
                                }
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                CommandSource source = ctx.getSource();
                                ConfigCache configCache = plugin.getConfigCache();
                                String arg1 = ctx.getArgument("group", String.class);
                                String arg2 = ctx.getArgument("command", String.class);

                                if (CWCommand.removeFromWhitelist(configCache, arg2, arg1))
                                    source.sendMessage(CWCommand.miniMessage.deserialize(String.format(configCache.prefix + configCache.removed_from_whitelist, arg2, arg1)));
                                else
                                    source.sendMessage(CWCommand.miniMessage.deserialize(String.format(configCache.prefix + configCache.group_doesnt_exist, arg1)));
                                return Command.SINGLE_SUCCESS;
                            })
                        )
                    )
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("dump")
                    .requires(src -> src.hasPermission(CWPermission.ADMIN.permission()))
                    .executes(ctx -> {
                        CommandSource source = ctx.getSource();
                        source.sendMessage(Component.text("Dumping all available commands to a file..."));
                        if (CommandUtil.dumpAllBukkitCommands(plugin.getServerCommands(), dataDirectory.resolve("command_dump.yml").toFile())) {
                            source.sendMessage(Component.text("Commands dumped to command_dump.yml"));
                        } else {
                            source.sendMessage(Component.text("Failed to save the file."));
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("help")
                    .executes(ctx -> {
                        CommandSource source = ctx.getSource();
                        source.sendMessage(CWCommand.helpComponent("cw", source.hasPermission(CWPermission.RELOAD.permission()), source.hasPermission(CWPermission.ADMIN.permission())));
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .build();

        final BrigadierCommand command = new BrigadierCommand(node);
        commandManager.register(commandManager.metaBuilder(command).plugin(plugin).build(), command);
    }
}
