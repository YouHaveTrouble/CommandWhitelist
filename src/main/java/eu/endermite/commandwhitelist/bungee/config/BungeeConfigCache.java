package eu.endermite.commandwhitelist.bungee.config;

import eu.endermite.commandwhitelist.bungee.CommandWhitelistBungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BungeeConfigCache {

    private final Configuration config;
    private final HashMap<String, List<String>> permList = new HashMap<>();
    private final String prefix, commandDenied, noPermission, noSubCommand, configReloaded, whitelistedCommand,
            removedWhitelistedCommand, noSuchGroup;
    private List<String> commandDeniedList;

    public BungeeConfigCache(Configuration config) {

        this.config = config;

        prefix = config.getString("messages.prefix");
        commandDenied = config.getString("messages.command-denied", null);
        commandDeniedList = config.getStringList("messages.command-denied");
        noPermission = config.getString("messages.no-permission");
        noSubCommand = config.getString("messages.no-such-subcommand");
        configReloaded = config.getString("messages.config-reloaded");
        whitelistedCommand = config.getString("messages.added-to-whitelist", "&eWhitelisted command &6%s &efor permission &6%s");
        removedWhitelistedCommand = config.getString("messages.removed-from-whitelist", "&eRemoved command &6%s &efrom permission &6%s");
        noSuchGroup = config.getString("messages.group-doesnt-exist", "&cGroup %s doesn't exist");

        Collection<String> perms = config.getSection("commands").getKeys();
        for (String s : perms) {
            this.permList.put(s, config.getStringList("commands."+s));
        }
    }

    public HashMap<String, List<String>> getPermList() {
        return permList;
    }
    public boolean addCommand(String command, String group) {
        try {
            this.permList.get(group).add(command);
            this.config.set("commands."+group, permList.get(group));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(CommandWhitelistBungee.getPlugin().getDataFolder(), "config.yml"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean removeCommand(String command, String group) {
        try {
            this.permList.get(group).remove(command);
            this.config.set("commands."+group, permList.get(group));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(CommandWhitelistBungee.getPlugin().getDataFolder(), "config.yml"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
}