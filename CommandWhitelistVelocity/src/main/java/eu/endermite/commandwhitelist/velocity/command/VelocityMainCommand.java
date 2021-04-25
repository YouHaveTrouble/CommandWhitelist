package eu.endermite.commandwhitelist.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import eu.endermite.commandwhitelist.velocity.CommandWhitelistVelocity;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class VelocityMainCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (args.length > 0) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (source.hasPermission("commandwhitelist.reload")) {
                    CommandWhitelistVelocity.reloadConfig(source);
                } else {
                    source.sendMessage(Component.text(CommandWhitelistVelocity.getConfigCache().no_permission));
                }
            }
        } else {
            source.sendMessage(Component.text("&bCommand Whitelist by YouHaveTrouble".replaceAll("&", "§")));
            if (source.hasPermission("commandwhitelist.reload")) {
                source.sendMessage(Component.text("&9/vcw reload &b- Reload velocity plugin configuration".replaceAll("&", "§")));
            }
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        return CompletableFuture.supplyAsync(() -> {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                if (source.hasPermission("commandwhitelist.reload") && "reload".startsWith(args[0]))
                    suggestions.add("reload");
            }
            return suggestions;
        });
    }
}
