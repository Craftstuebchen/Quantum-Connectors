package com.github.ysl3000.quantum.impl.commands;

import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ysl3000
 */
public class AbstractCommand implements ICommand<CommandSender> {

    private Map<String, ICommand<CommandSender>> subCommands = new HashMap<>();


    public void registerCommand(ICommand command) {
        subCommands.put(command.getName(), command);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, String[] strings) {
        if (strings.length > 0) {
            ICommand<CommandSender> iCommand = subCommands.get(strings[0]);
            if (iCommand != null && commandSenderIsAllowedToUse(iCommand, commandSender)) {
                String[] subArgs = Arrays.copyOfRange(strings, 1, strings.length);
                return iCommand.onCommand(commandSender, subArgs);// extract subset of args
            }
        }
        printHelp(commandSender);
        return true;
    }

    private boolean commandSenderIsAllowedToUse(ICommand<CommandSender> iCommand, CommandSender commandSender) {
        return iCommand.getAllowedCommandExecuter() == commandSender.getClass()
                && commandSender.hasPermission(iCommand.getPermission());
    }

    @Override
    public String getName() {
        return "qc";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public Class<CommandSender> getAllowedCommandExecuter() {
        return CommandSender.class;
    }

    @Override
    public void printHelp(CommandSender commandSender) {
        subCommands.entrySet().stream()
                .filter(entry -> commandSenderIsAllowedToUse(entry.getValue(), commandSender))
                .forEach(entry -> entry.getValue().printHelp(commandSender));
    }
}
