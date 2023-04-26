package eu.endermite.commandwhitelist.folia;

import eu.endermite.commandwhitelist.folia.command.FoliaCommandExecutor;
import eu.endermite.commandwhitelist.folia.listeners.AsyncTabCompleteBlockerListener;
import eu.endermite.commandwhitelist.folia.listeners.PlayerCommandPreProcessListener;
import eu.endermite.commandwhitelist.folia.listeners.PlayerCommandSendListener;
import eu.endermite.commandwhitelist.folia.listeners.TabCompleteBlockerListener;
import eu.endermite.commandwhitelist.folia.listeners.protocollib.PacketCommandPreProcessListener;
import eu.endermite.commandwhitelist.common.CWGroup;
import eu.endermite.commandwhitelist.common.ConfigCache;
import eu.endermite.commandwhitelist.common.commands.CWCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class CommandWhitelistFolia extends JavaPlugin {

    private static CommandWhitelistFolia commandWhitelist;
    private static ConfigCache configCache;
    private static BukkitAudiences audiences;

    @Override
    public void onEnable() {

        commandWhitelist = this;
        audiences = BukkitAudiences.create(this);

        reloadPluginConfig();

        Plugin protocollib = getServer().getPluginManager().getPlugin("ProtocolLib");

        if (!getConfigCache().useProtocolLib || protocollib == null || !protocollib.isEnabled()) {
            getServer().getPluginManager().registerEvents(new PlayerCommandPreProcessListener(), this);
        } else {
            PacketCommandPreProcessListener.protocol(this);
            getLogger().warning("Using ProtocolLib for command filter!");
            getLogger().warning("Please make sure you actually need this. This is not a \"better way to do it\".");
        }
        try {
            // Use paper's async tab completions if possible
            Class.forName("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent");
            getServer().getPluginManager().registerEvents(new AsyncTabCompleteBlockerListener(), this);
        } catch (ClassNotFoundException ignored) {
        }
        getServer().getPluginManager().registerEvents(new TabCompleteBlockerListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandSendListener(), this);

        PluginCommand command = getCommand("commandwhitelist");
        if (command != null) {
            FoliaCommandExecutor executor = new FoliaCommandExecutor();
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        new Metrics(this, 8705);
    }

    private void reloadPluginConfig() {
        File configFile = new File("plugins/CommandWhitelist/config.yml");
        if (configCache == null) {
            try {
                configCache = new ConfigCache(configFile, true, getSLF4JLogger());
            } catch (NoSuchMethodError e) {
                configCache = new ConfigCache(configFile, true, null);
            }
            return;
        }
        configCache.reloadConfig();
    }

    public void reloadPluginConfig(CommandSender sender) {
        getServer().getAsyncScheduler().runNow(this, (async) -> {
            reloadPluginConfig();
            try {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.updateCommands();
                }
            } catch (Exception ignored) {}
            audiences.sender(sender).sendMessage(CWCommand.miniMessage.deserialize(configCache.prefix + configCache.config_reloaded));
        });
    }

    public static CommandWhitelistFolia getPlugin() {
        return commandWhitelist;
    }

    public static ConfigCache getConfigCache() {
        return configCache;
    }

    public static BukkitAudiences getAudiences() {
        return audiences;
    }

    /**
     * @param player Bukkit Player
     * @return commands available to the player
     */
    public static HashSet<String> getCommands(org.bukkit.entity.Player player) {
        HashSet<String> commandList = new HashSet<>();
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                commandList.addAll(s.getValue().getCommands());
            else if (player.hasPermission(s.getValue().getPermission()))
                commandList.addAll(s.getValue().getCommands());
        }
        return commandList;
    }

    /**
     * @param player Bukkit Player
     * @return subcommands unavailable for the player
     */
    public static HashSet<String> getSuggestions(org.bukkit.entity.Player player) {
        HashSet<String> suggestionList = new HashSet<>();
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        for (Map.Entry<String, CWGroup> s : groups.entrySet()) {
            if (s.getKey().equalsIgnoreCase("default"))
                suggestionList.addAll(s.getValue().getSubCommands());
            if (player.hasPermission(s.getValue().getPermission())) continue;
            suggestionList.addAll(s.getValue().getSubCommands());
        }
        return suggestionList;
    }

    /**
     * @return Command denied message. Will use custom if command exists in any group.
     */
    public static String getCommandDeniedMessage(String command) {
        String commandDeniedMessage = configCache.command_denied;
        HashMap<String, CWGroup> groups = configCache.getGroupList();
        for (CWGroup group : groups.values()) {
            if (group.getCommands().contains(command)) {
                if (group.getCommandDeniedMessage() == null || group.getCommandDeniedMessage().isEmpty()) continue;
                commandDeniedMessage = group.getCommandDeniedMessage();
                break; // get first message we find
            }
        }
        return commandDeniedMessage;
    }

    public static ArrayList<String> getServerCommands() {
        try {
            return new ArrayList<>(Bukkit.getCommandMap().getKnownCommands().keySet());
        } catch (NoSuchMethodError error) {
            HashSet<String> commands = new HashSet<>();
            for (HelpTopic topic : Bukkit.getHelpMap().getHelpTopics()) {
                String cmd = topic.getName();
                if (Character.isUpperCase(cmd.charAt(0))) continue;
                commands.add(topic.getName());
            }
            return new ArrayList<>(commands);
        }
    }
}
