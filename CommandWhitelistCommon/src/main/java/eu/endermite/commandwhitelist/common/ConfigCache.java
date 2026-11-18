package eu.endermite.commandwhitelist.common;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import net.kyori.adventure.key.Key;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigCache {

    private static final String DEFAULT_COMMAND_DENIED_SOUND_KEY = "entity.villager.no";

    private final File configFile;
    private ConfigFile config;
    private final Object logger;
    private final boolean canDoProtocolLib, supportsCommandDeniedMessage;
    private volatile ConfigSnapshot snapshot = ConfigSnapshot.empty();

    public ConfigCache(File configFile, boolean canDoProtocolLib, boolean supportsCommandDeniedMessage, Object logger) {
        this.configFile = configFile;
        this.canDoProtocolLib = canDoProtocolLib;
        this.supportsCommandDeniedMessage = supportsCommandDeniedMessage;
        this.logger = logger;

        try {
            reloadConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean reloadConfig() {
        createFiles();

        final ConfigFile loadedConfig;
        try {
            loadedConfig = ConfigFile.loadConfig(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        loadedConfig.addDefault("messages.prefix", "CommandWhitelist > ");
        if (supportsCommandDeniedMessage) {
            loadedConfig.addDefault("messages.command_denied", "No such command.");
            loadedConfig.addDefault("messages.subcommand_denied", "You cannot use this subcommand");
        }

        loadedConfig.addDefault("messages.no_permission", "<red>You don't have permission to do this.");
        loadedConfig.addDefault("messages.no_such_subcommand", "<red>No subcommand by that name.");
        loadedConfig.addDefault("messages.config_reloaded", "<yellow>Configuration reloaded.");
        loadedConfig.addDefault("messages.added_to_whitelist", "<yellow>Whitelisted command <gold>%s <yellow>for permission <gold>%s");
        loadedConfig.addDefault("messages.removed_from_whitelist", "<yellow>Removed command <gold>%s <yellow>from permission <gold>%s");
        loadedConfig.addDefault("messages.group_doesnt_exist", "<red>Group doesn't exist or error occured");
        loadedConfig.addComment("messages", "Messages use MiniMessage formatting (https://docs.adventure.kyori.net/minimessage/format)");

        if (canDoProtocolLib) {
            loadedConfig.addDefault("use_protocollib", false,
                    "Do not enable if you don't have issues with aliased commands.\nThis requires server restart to take effect.");
        }

        if (supportsCommandDeniedMessage) {
            loadedConfig.addDefault("message_type", MessageType.CHAT.toString(),
                    "Valid message types are CHAT and ACTIONBAR and CHAT_AND_ACTIONBAR.");
            loadedConfig.addDefault("command_denied_sound.enabled", false);
            loadedConfig.addDefault("command_denied_sound.key", DEFAULT_COMMAND_DENIED_SOUND_KEY);
            loadedConfig.addDefault("command_denied_sound.volume", 1.0f);
            loadedConfig.addDefault("command_denied_sound.pitch", 1.0f);
        }

        if (loadedConfig.isNew()) {
            List<String> exampleCommands = new ArrayList<>();
            exampleCommands.add("example");
            List<String> exampleSubCommands = new ArrayList<>();
            exampleSubCommands.add("example of");
            String exampleCustomCommandDeniedMessage = "You don't have commandwhitelist.group.example permission.";

            loadedConfig.addExample("groups.example.commands", exampleCommands,
                    "This is the WHITELIST of commands that players will be able to see/use in the group \"example\"");
            loadedConfig.addExample("groups.example.subcommands", exampleSubCommands,
                    "This is the BLACKLIST of subcommands that players will NOT be able to see/use in the group \"example\"");
            loadedConfig.addExample("groups.example.custom_command_denied_message", exampleCustomCommandDeniedMessage,
                    "This is a custom message that players will see if they do not have commandwhitelist.group.<group_name> permission.\n" +
                            "commandwhitelist.group.example in this case\n" +
                            "If you don't want to use a custom message, set custom_command_denid_message: \"\"");
            loadedConfig.addComment("groups.example",
                    "All groups except from default require commandwhitelist.group.<group_name> permission\n" +
                            "commandwhitelist.group.example in this case\n" +
                            "If you wish to leave the list empty, put \"commands: []\" or \"subcommands: []\"");
        }

        loadedConfig.makeSectionLenient("groups");
        List<String> defaultCommands = Arrays.asList(
                "help", "spawn", "bal", "balance", "baltop", "pay", "r", "msg",
                "tpa", "tpahere", "tpaccept", "tpdeny", "warp"
        );
        List<String> defaultSubcommands = Collections.singletonList("help about");
        loadedConfig.addDefault("groups.default",
                new CWGroup("default", defaultCommands, defaultSubcommands, "").serialize());

        String prefix = loadedConfig.getString("messages.prefix");
        String commandDenied = loadedConfig.getString("messages.command_denied", "No such command.");
        String subcommandDenied = loadedConfig.getString("messages.subcommand_denied", "You cannot use this subcommand");
        String noPermission = loadedConfig.getString("messages.no_permission");
        String noSuchSubcommand = loadedConfig.getString("messages.no_such_subcommand");
        String configReloaded = loadedConfig.getString("messages.config_reloaded");
        String addedToWhitelist = loadedConfig.getString("messages.added_to_whitelist");
        String removedFromWhitelist = loadedConfig.getString("messages.removed_from_whitelist");
        String groupDoesntExist = loadedConfig.getString("messages.group_doesnt_exist");
        boolean useProtocolLib = loadedConfig.getBoolean("use_protocollib", false);
        boolean commandDeniedSoundEnabled = loadedConfig.getBoolean("command_denied_sound.enabled", false);
        String commandDeniedSoundKey = DEFAULT_COMMAND_DENIED_SOUND_KEY;
        float commandDeniedSoundVolume = 1.0f;
        float commandDeniedSoundPitch = 1.0f;

        if (commandDeniedSoundEnabled) {
            try {
                String configuredSoundKey = loadedConfig.getString("command_denied_sound.key");
                if (configuredSoundKey == null) {
                    warn("Invalid sound key. Using " + DEFAULT_COMMAND_DENIED_SOUND_KEY);
                } else {
                    try {
                        commandDeniedSoundKey = Key.key(configuredSoundKey).asString().toLowerCase(Locale.ROOT);
                    } catch (Exception e) {
                        warn("Invalid sound key. Using " + DEFAULT_COMMAND_DENIED_SOUND_KEY);
                    }
                }
                commandDeniedSoundVolume = loadedConfig.getFloat("command_denied_sound.volume");
                commandDeniedSoundPitch = loadedConfig.getFloat("command_denied_sound.pitch");
            } catch (Exception e) {
                warn("Invalid sound. Using default sound configurations.");
                commandDeniedSoundKey = DEFAULT_COMMAND_DENIED_SOUND_KEY;
                commandDeniedSoundVolume = 1.0f;
                commandDeniedSoundPitch = 1.0f;
            }
        }

        boolean debug = loadedConfig.getBoolean("debug", false);
        MessageType messageType = MessageType.CHAT;
        try {
            String chatTypeId = loadedConfig.getString("message_type");
            if (chatTypeId != null) {
                messageType = MessageType.valueOf(chatTypeId.toUpperCase(Locale.ROOT));
            } else {
                warn("Invalid message type. Using CHAT.");
            }
        } catch (IllegalArgumentException e) {
            warn("Invalid message type. Using CHAT.");
        }

        Map<String, CWGroup> loadedGroups = new LinkedHashMap<>();
        ConfigSection groupSection = loadedConfig.getConfigSection("groups");
        for (String key : groupSection.getKeys(false)) {
            loadedGroups.put(key, loadCWGroup(key, groupSection));
        }

        if (useProtocolLib) {
            warn("Protocollib support is deprecated and will be removed in future versions. If you have an actual" +
                    " use case for it (aka plugin does not work without it), please reach out via discord (https://discord.youhavetrouble.me).");
        }

        if (!saveConfig(loadedConfig)) return false;

        ConfigSnapshot loadedSnapshot = new ConfigSnapshot(
                prefix,
                commandDenied,
                noPermission,
                noSuchSubcommand,
                configReloaded,
                addedToWhitelist,
                removedFromWhitelist,
                groupDoesntExist,
                subcommandDenied,
                commandDeniedSoundKey,
                useProtocolLib,
                commandDeniedSoundEnabled,
                commandDeniedSoundVolume,
                commandDeniedSoundPitch,
                messageType,
                debug,
                loadedGroups
        );

        config = loadedConfig;
        snapshot = loadedSnapshot;
        return true;
    }

    private boolean saveConfig(ConfigFile target) {
        try {
            target.save();
            return true;
        } catch (Exception e) {
            warn("CommandWhitelist - Failed to save configuration: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void createFiles() {
        try {
            File parent = configFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Could not create configuration directory: " + parent);
            }
            if (!configFile.exists() && !configFile.createNewFile()) {
                throw new IOException("Could not create configuration file: " + configFile);
            }
        } catch (IOException e) {
            warn("CommandWhitelist - Failed to create configuration file: " + e.getMessage());
        }
    }

    public CWGroup loadCWGroup(String id, ConfigSection section) {
        HashSet<String> commands = new HashSet<>();
        for (String command : section.getStringList(id + ".commands")) {
            if (command.contains(" ")) {
                String[] commandSplit = command.split(" ");
                warn("CommandWhitelist - \"" + command + "\" is not a command. Loading it as \"" + commandSplit[0] + "\".");
                command = commandSplit[0];
            }
            commands.add(command);
        }

        List<String> subCommands = new ArrayList<>();
        for (String subCommand : section.getStringList(id + ".subcommands")) {
            if (!subCommand.contains(" ")) {
                warn("CommandWhitelist - \"" + subCommand + "\" is not a subcommand. Skipping it.");
                continue;
            }
            subCommands.add(subCommand);
        }

        String customCommandDeniedMessage = section.getString(id + ".custom_command_denied_message");
        return new CWGroup(id, commands, subCommands, customCommandDeniedMessage);
    }

    public synchronized boolean addCommandToGroup(String id, String command) {
        ConfigSnapshot current = snapshot;
        CWGroup currentGroup = current.groups.get(id);
        if (currentGroup == null || config == null) return false;

        CWGroup updatedGroup = currentGroup.withCommandAdded(command);
        Map<String, CWGroup> updatedGroups = new LinkedHashMap<>(current.groups);
        updatedGroups.put(id, updatedGroup);

        final ConfigFile updatedConfig;
        try {
            // Work on a fresh instance so a failed save cannot leak uncommitted edits.
            updatedConfig = ConfigFile.loadConfig(configFile);
        } catch (Exception e) {
            warn("CommandWhitelist - Failed to reload configuration before saving a group: " + e.getMessage());
            return false;
        }
        updatedConfig.set("groups." + id, updatedGroup.serialize());
        if (!saveConfig(updatedConfig)) return false;

        config = updatedConfig;
        snapshot = current.withGroups(updatedGroups);
        return true;
    }

    public synchronized boolean removeCommandFromGroup(String id, String command) {
        ConfigSnapshot current = snapshot;
        CWGroup currentGroup = current.groups.get(id);
        if (currentGroup == null || config == null) return false;

        CWGroup updatedGroup = currentGroup.withCommandRemoved(command);
        Map<String, CWGroup> updatedGroups = new LinkedHashMap<>(current.groups);
        updatedGroups.put(id, updatedGroup);

        final ConfigFile updatedConfig;
        try {
            // Work on a fresh instance so a failed save cannot leak uncommitted edits.
            updatedConfig = ConfigFile.loadConfig(configFile);
        } catch (Exception e) {
            warn("CommandWhitelist - Failed to reload configuration before saving a group: " + e.getMessage());
            return false;
        }
        updatedConfig.set("groups." + id, updatedGroup.serialize());
        if (!saveConfig(updatedConfig)) return false;

        config = updatedConfig;
        snapshot = current.withGroups(updatedGroups);
        return true;
    }

    public ConfigSnapshot getSnapshot() {
        return snapshot;
    }

    public Map<String, CWGroup> getGroupList() {
        return snapshot.groups;
    }

    public void warn(String log) {
        if (logger == null) {
            System.out.println("WARNING: " + log);
            return;
        }
        if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).warn(log);
            return;
        }
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).warning(log);
        }
    }

    public void debug(String log) {
        if (!snapshot.debug) return;
        if (logger == null) {
            System.out.println("DEBUG: " + log);
            return;
        }
        if (logger instanceof org.slf4j.Logger) {
            ((org.slf4j.Logger) logger).info(log);
            return;
        }
        if (logger instanceof java.util.logging.Logger) {
            ((java.util.logging.Logger) logger).info(log);
        }
    }
}
