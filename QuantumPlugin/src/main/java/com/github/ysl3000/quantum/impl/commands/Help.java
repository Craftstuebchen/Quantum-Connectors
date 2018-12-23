package com.github.ysl3000.quantum.impl.commands;

import com.github.ysl3000.quantum.impl.interfaces.ICircuitActivator;
import com.github.ysl3000.quantum.impl.utils.MessageLogger;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by ysl3000
 */
public class Help implements ICommand<Player> {
    private MessageLogger messageLogger;
    private ICircuitActivator circuitManager;

    public Help(MessageLogger messageLogger, ICircuitActivator circuitManager) {
        this.messageLogger = messageLogger;
        this.circuitManager = circuitManager;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        messageLogger.msg(player, messageLogger.getMessage("usage"));

        messageLogger.msg(player, ChatColor.YELLOW +
                messageLogger.getMessage("available_circuits") + ChatColor.WHITE +
                circuitManager.getValidSendersAsString());

        return false;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "qc.help";
    }

    @Override
    public Class<Player> getAllowedCommandExecuter() {
        return Player.class;
    }

    @Override
    public void printHelp(CommandSender commandSender) {
        commandSender.spigot().sendMessage(new TextComponent("Displays the usage of QuantumConnectors"));
    }
}
