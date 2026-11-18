package eu.endermite.commandwhitelist.common;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable view of the complete active plugin configuration.
 * A reload publishes one new instance so readers never observe a mixture of old and new settings.
 */
public final class ConfigSnapshot {

    public final String prefix;
    public final String command_denied;
    public final String no_permission;
    public final String no_such_subcommand;
    public final String config_reloaded;
    public final String added_to_whitelist;
    public final String removed_from_whitelist;
    public final String group_doesnt_exist;
    public final String subcommand_denied;
    public final String command_denied_sound_key;

    public final boolean useProtocolLib;
    public final boolean command_denied_sound_enabled;
    public final float command_denied_sound_volume;
    public final float command_denied_sound_pitch;
    public final MessageType messageType;
    public final boolean debug;
    public final Map<String, CWGroup> groups;

    public ConfigSnapshot(
            String prefix,
            String commandDenied,
            String noPermission,
            String noSuchSubcommand,
            String configReloaded,
            String addedToWhitelist,
            String removedFromWhitelist,
            String groupDoesntExist,
            String subcommandDenied,
            String commandDeniedSoundKey,
            boolean useProtocolLib,
            boolean commandDeniedSoundEnabled,
            float commandDeniedSoundVolume,
            float commandDeniedSoundPitch,
            MessageType messageType,
            boolean debug,
            Map<String, CWGroup> groups
    ) {
        this.prefix = prefix;
        this.command_denied = commandDenied;
        this.no_permission = noPermission;
        this.no_such_subcommand = noSuchSubcommand;
        this.config_reloaded = configReloaded;
        this.added_to_whitelist = addedToWhitelist;
        this.removed_from_whitelist = removedFromWhitelist;
        this.group_doesnt_exist = groupDoesntExist;
        this.subcommand_denied = subcommandDenied;
        this.command_denied_sound_key = commandDeniedSoundKey;
        this.useProtocolLib = useProtocolLib;
        this.command_denied_sound_enabled = commandDeniedSoundEnabled;
        this.command_denied_sound_volume = commandDeniedSoundVolume;
        this.command_denied_sound_pitch = commandDeniedSoundPitch;
        this.messageType = messageType;
        this.debug = debug;
        this.groups = Collections.unmodifiableMap(new LinkedHashMap<>(groups));
    }

    public static ConfigSnapshot empty() {
        return new ConfigSnapshot(
                "CommandWhitelist > ",
                "No such command.",
                "<red>You don't have permission to do this.",
                "<red>No subcommand by that name.",
                "<yellow>Configuration reloaded.",
                "<yellow>Whitelisted command <gold>%s <yellow>for permission <gold>%s",
                "<yellow>Removed command <gold>%s <yellow>from permission <gold>%s",
                "<red>Group doesn't exist or error occured",
                "You cannot use this subcommand",
                "entity.villager.no",
                false,
                false,
                1.0f,
                1.0f,
                MessageType.CHAT,
                false,
                Collections.emptyMap()
        );
    }

    public ConfigSnapshot withGroups(Map<String, CWGroup> updatedGroups) {
        return new ConfigSnapshot(
                prefix,
                command_denied,
                no_permission,
                no_such_subcommand,
                config_reloaded,
                added_to_whitelist,
                removed_from_whitelist,
                group_doesnt_exist,
                subcommand_denied,
                command_denied_sound_key,
                useProtocolLib,
                command_denied_sound_enabled,
                command_denied_sound_volume,
                command_denied_sound_pitch,
                messageType,
                debug,
                updatedGroups
        );
    }
}
