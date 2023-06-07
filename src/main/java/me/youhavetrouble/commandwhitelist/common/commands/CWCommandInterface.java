package me.youhavetrouble.commandwhitelist.common.commands;

import java.util.Collection;

public interface CWCommandInterface {

    void execute(String[] args);

    Collection<String> tabComplete(String[] args);

    String getPermission();

}
