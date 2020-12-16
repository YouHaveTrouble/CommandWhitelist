package eu.endermite.commandwhitelist.velocity.config;

import com.moandjiezana.toml.Toml;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VelocityConfigCache {

    private HashMap<String, List<String>> permList = new HashMap<>();
    private final String noPermission, noSubCommand, configReloaded;


    public VelocityConfigCache(Toml config) {

        Toml messages = config.getTable("messages");
        noPermission = messages.getString("no-permission", "&cYou don't have permission to do this.");
        noSubCommand = messages.getString("no-such-subcommand", "&cNo subcommand by that name.");
        configReloaded = messages.getString("config-reloaded", "&eConfiguration reloaded.");

        Toml groups = config.getTable("commands");

        for (Map.Entry<String, Object> set : groups.entrySet()) {
            this.permList.put(set.getKey(), (List<String>) set.getValue());
        }
    }

    public HashMap<String, List<String>> getPermList() {
        return permList;
    }
    public String getNoPermission() {
        return noPermission.replaceAll("&", "§");
    }
    public String getNoSubCommand() {return  noSubCommand.replaceAll("&", "§");}
    public String getConfigReloaded() {return  configReloaded.replaceAll("&", "§");}


}
