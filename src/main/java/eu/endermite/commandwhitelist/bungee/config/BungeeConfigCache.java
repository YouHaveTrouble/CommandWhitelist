package eu.endermite.commandwhitelist.bungee.config;

import net.md_5.bungee.config.Configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BungeeConfigCache {

    private final HashMap<String, List<String>> permList = new HashMap<>();
    private final String prefix, commandDenied, noPermission, noSubCommand, configReloaded;
    private List<String> commandDeniedList;

    public BungeeConfigCache(Configuration config) {

        prefix = config.getString("messages.prefix");
        commandDenied = config.getString("messages.command-denied", null);
        commandDeniedList = config.getStringList("messages.command-denied");
        noPermission = config.getString("messages.no-permission");
        noSubCommand = config.getString("messages.no-such-subcommand");
        configReloaded = config.getString("messages.config-reloaded");

        Collection<String> perms = config.getSection("commands").getKeys();
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
