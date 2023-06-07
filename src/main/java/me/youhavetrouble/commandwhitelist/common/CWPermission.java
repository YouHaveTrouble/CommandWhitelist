package me.youhavetrouble.commandwhitelist.common;

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
     *
     */
    public static String getGroupPermission(String groupId) {
        return "commandwhitelist.group." + groupId;
    }

}
