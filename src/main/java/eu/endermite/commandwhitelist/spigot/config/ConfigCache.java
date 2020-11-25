package eu.endermite.commandwhitelist.spigot.config;

import eu.endermite.commandwhitelist.spigot.CommandWhitelist;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConfigCache {

    private FileConfiguration config;
    private HashMap<String, List<String>> permList = new HashMap<>();
    private HashMap<String, List<String>> permSubList = new HashMap<>();
    private final String prefix, commandDenied, noPermission, noSubCommand, configReloaded, whitelistedCommand,
            removedWhitelistedCommand, noSuchGroup, subCommandDenied;
    private final List<String> commandDeniedList;

    public ConfigCache(FileConfiguration config) {

        this.config = config;

        prefix = config.getString("messages.prefix", "");
        commandDenied = config.getString("messages.command-denied", null);
        commandDeniedList = config.getStringList("messages.command-denied");
        subCommandDenied = config.getString("messages.subcommand-denied", "You cannot use this subcommand");
        noPermission = config.getString("messages.no-permission", "&cYou don't have permission to do this.");
        noSubCommand = config.getString("messages.no-such-subcommand", "&cNo subcommand by that name.");
        configReloaded = config.getString("messages.config-reloaded", "&eConfiguration reloaded.");
        whitelistedCommand = config.getString("messages.added-to-whitelist", "&eWhitelisted command &6%s &efor permission &6%s");
        removedWhitelistedCommand = config.getString("messages.removed-from-whitelist", "&eRemoved command &6%s &efrom permission &6%s");
        noSuchGroup = config.getString("messages.group-doesnt-exist", "&cGroup %s doesn't exist");

        Set<String> perms = config.getConfigurationSection("commands").getKeys(false);
        for (String s : perms) {
            this.permList.put(s, config.getStringList("commands."+s));
        }

        Set<String> subperms = config.getConfigurationSection("tabcompletions").getKeys(false);
        for (String s : subperms) {
            this.permSubList.put(s, config.getStringList("tabcompletions."+s));
        }
    }

    public HashMap<String, List<String>> getPermList() {
        return permList;
    }
    public HashMap<String, List<String>> getPermSubList() {
        return permSubList;
    }
    public boolean addCommand(String command, String group) {
        try {
            if (this.permList.get(group).contains(command)) {
                return true;
            }
            this.permList.get(group).add(command);
            this.config.set("commands."+group, permList.get(group));
            config.save(CommandWhitelist.getPlugin().getDataFolder()+"/config.yml");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.updateCommands();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean removeCommand(String command, String group) {
        try {
            this.permList.get(group).remove(command);
            this.config.set("commands."+group, permList.get(group));
            config.save(CommandWhitelist.getPlugin().getDataFolder()+"/config.yml");
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.updateCommands();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public String getPrefix() {return prefix;}
    public String getCommandDenied() {return commandDenied;}
    public List<String> getCommandDeniedList() {
        return commandDeniedList;
    }
    public String getNoPermission() {return noPermission;}
    public String getNoSubCommand() {return  noSubCommand;}
    public String getConfigReloaded() {return  configReloaded;}
    public String getWhitelistedCommand() {
        return whitelistedCommand;
    }
    public String getRemovedWhitelistedCommand() {
        return removedWhitelistedCommand;
    }
    public String getNoSuchGroup() {
        return noSuchGroup;
    }
    public String getSubCommandDenied() {
        return subCommandDenied;
    }
}