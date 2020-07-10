package eu.endermite.commandwhitelist.config;

import eu.endermite.commandwhitelist.CommandWhitelist;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ConfigCache {

    private final HashMap<String, List<String>> permList = new HashMap<>();
    private final String prefix;
    private final  String commandDenied;

    public ConfigCache(FileConfiguration yamlConfiguration) {
        Set<String> perms = yamlConfiguration.getConfigurationSection("commands").getKeys(false);
        for (String s : perms) {
            this.permList.put(s, CommandWhitelist.getPlugin().getConfig().getStringList("commands."+s));
        }

        this.prefix = CommandWhitelist.getPlugin().getConfig().getString("messages.prefix");
        this.commandDenied = CommandWhitelist.getPlugin().getConfig().getString("messages.command-denied");
    }

    public HashMap<String, List<String>> getPermList() {
        return permList;
    }

    public List<String> getPerm(String s) {
        return permList.get(s);
    }
    public String getPrefix() {return prefix;}
    public String getCommandDenied() {return commandDenied;}
}
