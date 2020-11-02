package eu.endermite.commandwhitelist.spigot.config;

import org.bukkit.configuration.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConfigCache {

    private HashMap<String, List<String>> permList = new HashMap<>();
    private String prefix, commandDenied, noPermission, noSubCommand, configReloaded;
    private List<String> commandDeniedList;

    public ConfigCache(Configuration config) {

        prefix = config.getString("messages.prefix");
        commandDenied = config.getString("messages.command-denied", null);
        commandDeniedList = config.getStringList("messages.command-denied");
        noPermission = config.getString("messages.no-permission");
        noSubCommand = config.getString("messages.no-such-subcommand");
        configReloaded = config.getString("messages.config-reloaded");

        Set<String> perms = config.getConfigurationSection("commands").getKeys(false);
        for (String s : perms) {
            this.permList.put(s, config.getStringList("commands."+s));
        }
    }

    public HashMap<String, List<String>> getPermList() {
        return permList;
    }

    public String getPrefix() {return prefix;}
    public String getCommandDenied() {return commandDenied;}
    public List<String> getCommandDeniedList() {
        return commandDeniedList;
    }
    public String getNoPermission() {return noPermission;}
    public String getNoSubCommand() {return  noSubCommand;}
    public String getConfigReloaded() {return  configReloaded;}
}
