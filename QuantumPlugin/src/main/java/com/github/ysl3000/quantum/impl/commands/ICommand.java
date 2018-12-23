package com.github.ysl3000.quantum.impl.commands;

import org.bukkit.command.CommandSender;

/**
 * Created by ysl3000
 */
public interface ICommand<T extends CommandSender> {
    boolean onCommand(T commandSender, String[] args);

    String getName();

    String getPermission();

    Class<T> getAllowedCommandExecuter();

    void printHelp(CommandSender commandSender);
}
