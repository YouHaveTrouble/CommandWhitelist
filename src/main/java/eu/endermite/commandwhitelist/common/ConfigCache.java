package eu.endermite.commandwhitelist.common;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class ConfigCache {

    private final HashMap<String, CWGroup> groupList = new LinkedHashMap<>();
    public String prefix, command_denied, no_permission, no_such_subcommand, config_reloaded, added_to_whitelist,
            removed_from_whitelist, group_doesnt_exist, subcommand_denied;
    public boolean useProtocolLib = false;

    public ConfigCache(File configFile, boolean canDoProtocolLib) {
        if (!reloadConfig(configFile, canDoProtocolLib))
            reloadConfig(configFile, canDoProtocolLib);
    }

    public void saveDefaultConfig(Map<String, Object> data, File configFile, boolean canDoProtocolLib) {

        data.put("messages", processMessages());

        if (canDoProtocolLib) {
            data.put("use_protocollib_to_detect_commands", false);
        }

        List<String> defaultCommands = new ArrayList<>();
        List<String> defaultSubcommands = new ArrayList<>();
        defaultCommands.add("help");
        defaultCommands.add("spawn");
        defaultCommands.add("bal");
        defaultCommands.add("balance");
        defaultCommands.add("baltop");
        defaultCommands.add("pay");
        defaultCommands.add("r");
        defaultCommands.add("msg");
        defaultCommands.add("tpa");
        defaultCommands.add("tpahere");
        defaultCommands.add("tpaccept");
        defaultCommands.add("tpdeny");
        defaultCommands.add("warp");

        defaultSubcommands.add("help about");

        HashMap<String, Object> groups = new LinkedHashMap<>();
        groups.put("default", new CWGroup("default", defaultCommands, defaultSubcommands).serialize());

        data.putIfAbsent("groups", groups);

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setAllowUnicode(true);
        Yaml yaml = new Yaml(dumperOptions);
        try {
            FileWriter writer = new FileWriter(configFile.getPath());
            yaml.dump(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean reloadConfig(File configFile, boolean canDoProtocolLib) {
        HashMap<String, Object> config = new LinkedHashMap<>();
        Yaml yaml = new Yaml();
        try {
            FileInputStream fileInputStream = new FileInputStream(configFile);
            config = yaml.load(fileInputStream);
        } catch (FileNotFoundException ignored) {
            saveDefaultConfig(config, configFile, canDoProtocolLib);
            return false;
        }

        HashMap<String, String> messages = (HashMap<String, String>) config.get("messages");
        this.prefix = messages.get("prefix");
        this.command_denied = messages.get("command_denied");
        this.no_such_subcommand = messages.get("no_such_subcommand");
        this.no_permission = messages.get("no_permission");
        this.config_reloaded = messages.get("config_reloaded");
        this.added_to_whitelist = messages.get("added_to_whitelist");
        this.removed_from_whitelist = messages.get("removed_from_whitelist");
        this.group_doesnt_exist = messages.get("group_doesnt-exist");
        this.subcommand_denied = messages.get("subcommand_denied");

        if (canDoProtocolLib)
            this.useProtocolLib = (boolean) config.get("use_protocollib_to_detect_commands");



        HashMap<String, HashMap<String, Object>> groups = (HashMap<String, HashMap<String, Object>>) config.get("groups");
        for (Map.Entry<String, HashMap<String, Object>> entry : groups.entrySet()) {
            groupList.put(entry.getKey(), loadCWGroup(entry.getKey(), entry.getValue()));
        }

        saveDefaultConfig(config, configFile, canDoProtocolLib);

        return true;
    }

    public CWGroup loadCWGroup(String id, HashMap<String, Object> map) {
        List<String> subCommands = new ArrayList<>((Collection<? extends String>) map.get("subcommands"));
        List<String> commands = new ArrayList<>((Collection<? extends String>) map.get("commands"));
        return new CWGroup(id, commands, subCommands);
    }

    public HashMap<String, CWGroup> getGroupList() {
        return groupList;
    }

    public HashMap<String, String> processMessages() {
        HashMap<String, String> messages = new LinkedHashMap<>();
        messages.put("prefix", stringOrDefault(prefix, "CommandWhitelist > "));
        messages.put("command_denied", stringOrDefault(command_denied, "No such command."));
        messages.put("subcommand_denied", stringOrDefault(subcommand_denied, "You cannot use this subcommand"));
        messages.put("no_permission", stringOrDefault(no_permission, "<red>You don't have permission to do this."));
        messages.put("no_such_subcommand",  stringOrDefault(no_such_subcommand, "<red>No subcommand by that name."));
        messages.put("config_reloaded", stringOrDefault(config_reloaded, "<yellow>Configuration reloaded."));
        messages.put("added_to_whitelist", stringOrDefault(added_to_whitelist, "<yellow>Whitelisted command <orange>%s <yellow>for permission <orange>%s"));
        messages.put("removed_from_whitelist", stringOrDefault(removed_from_whitelist, "<yellow>Removed command <orange>%s <yellow>from permission <orange>%s"));
        messages.put("group_doesnt_exist", stringOrDefault(group_doesnt_exist, "<red>Group doesn't exist or error occured"));
        return messages;
    }

    public String stringOrDefault(String value, String def) {
        if (value != null)
            return value;
        else
            return def;
    }

}
