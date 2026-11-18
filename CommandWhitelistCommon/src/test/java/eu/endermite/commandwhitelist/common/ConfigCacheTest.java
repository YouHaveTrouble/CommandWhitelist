package eu.endermite.commandwhitelist.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConfigCacheTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void reloadReplacesGroupsAndPreservesImmutableSnapshots() throws IOException {
        Path configFile = temporaryDirectory.resolve("config.yml");
        Files.writeString(configFile, """
                groups:
                  default:
                    commands:
                      - spawn
                    subcommands: []
                  legacy:
                    commands:
                      - op
                    subcommands: []
                """);

        ConfigCache cache = new ConfigCache(configFile.toFile(), false, false, null);
        Map<String, CWGroup> previousSnapshot = cache.getGroupList();

        assertTrue(previousSnapshot.containsKey("legacy"));
        assertThrows(UnsupportedOperationException.class, () -> previousSnapshot.clear());
        assertThrows(UnsupportedOperationException.class,
                () -> previousSnapshot.get("legacy").getCommands().add("deop"));

        Files.writeString(configFile, """
                groups:
                  default:
                    commands:
                      - spawn
                    subcommands: []
                """);

        assertTrue(cache.reloadConfig());
        assertFalse(cache.getGroupList().containsKey("legacy"));
        assertFalse(cache.addCommandToGroup("legacy", "resurrected"),
                "An update must not recreate a group removed by reload");
        assertTrue(previousSnapshot.containsKey("legacy"),
                "Readers holding the previous snapshot must not see it mutated");
        assertNotSame(previousSnapshot, cache.getGroupList());
    }


    @Test
    void reloadPublishesAllSettingsAsOneImmutableSnapshot() throws IOException {
        Path configFile = temporaryDirectory.resolve("snapshot-config.yml");
        Files.writeString(configFile, """
                messages:
                  prefix: "Before > "
                message_type: CHAT
                command_denied_sound:
                  enabled: false
                groups:
                  default:
                    commands:
                      - spawn
                    subcommands: []
                  legacy:
                    commands:
                      - op
                    subcommands: []
                """);

        ConfigCache cache = new ConfigCache(configFile.toFile(), false, true, null);
        ConfigSnapshot previous = cache.getSnapshot();

        Files.writeString(configFile, """
                messages:
                  prefix: "After > "
                message_type: ACTIONBAR
                command_denied_sound:
                  enabled: true
                  key: entity.villager.no
                  volume: 0.5
                  pitch: 0.75
                groups:
                  default:
                    commands:
                      - spawn
                    subcommands: []
                """);

        assertTrue(cache.reloadConfig());
        ConfigSnapshot current = cache.getSnapshot();

        assertNotSame(previous, current);
        assertEquals("Before > ", previous.prefix);
        assertEquals(MessageType.CHAT, previous.messageType);
        assertFalse(previous.command_denied_sound_enabled);
        assertTrue(previous.groups.containsKey("legacy"));

        assertEquals("After > ", current.prefix);
        assertEquals(MessageType.ACTIONBAR, current.messageType);
        assertTrue(current.command_denied_sound_enabled);
        assertFalse(current.groups.containsKey("legacy"));

        assertTrue(cache.addCommandToGroup("default", "fly"));
        ConfigSnapshot afterAdminUpdate = cache.getSnapshot();
        assertEquals("After > ", afterAdminUpdate.prefix);
        assertEquals(MessageType.ACTIONBAR, afterAdminUpdate.messageType);
        assertTrue(afterAdminUpdate.command_denied_sound_enabled);
    }

    @Test
    void commandUpdatesCreateNewGroupsInsteadOfMutatingExistingOnes() {
        CWGroup original = new CWGroup("default", List.of("spawn"), List.of(), "");
        CWGroup updated = original.withCommandAdded("fly");

        assertFalse(original.getCommands().contains("fly"));
        assertTrue(updated.getCommands().contains("fly"));
    }
}
