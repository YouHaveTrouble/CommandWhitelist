package eu.endermite.commandwhitelist.common;

public enum CWPermission {

    ADMIN("commandwhitelist.admin"),
    RELOAD("commandwhitelist.reload"),
    BYPASS("commandwhitelist.bypass");

    private final String permission;

    CWPermission(String permission) {
        this.permission = permission;
    }

    public String permission() {
        return permission;
    }

    /**
     * Allows to check specific group permission
     * @param configCache
     * @param groupId
     * @return
     */
    public static String getGroupPermission(ConfigCache configCache, String groupId) {
        if (configCache.getGroupList().containsKey(groupId))
            return configCache.getGroupList().get(groupId).getPermission();
        return null;
    }

}
