package com.github.ysl3000.quantum.impl.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads all Messages from message.yml
 * Provides support for player messages
 */
public class MessageLogger {

    private Map<String, String> messages;
    private Logger logger;
    private boolean verbose;

    public MessageLogger(Logger logger, Map<String, String> messages) {
        this.logger = logger;
        this.messages = messages;
    }

    public void msg(Player player, String sMessage) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "[QC] " + ChatColor.WHITE + sMessage);
    }

    public void verbose(String message) {
        if (isVerbose()) log(message);
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void log(String sMessage) {
        log(Level.INFO, sMessage);
    }

    //Generic wrappers for console messages
    public void log(Level level, String sMessage) {
        if (!sMessage.equals(""))
            logger.log(level, sMessage);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    //Wrapper for getting localized messages
    public String getMessage(String sMessageName) {
        return (messages.get(sMessageName));
    }

    public void verboseError(String message) {
        if (isVerbose()) error(message);
    }

    public void error(String sMessage) {
        log(Level.WARNING, sMessage);
    }
}
