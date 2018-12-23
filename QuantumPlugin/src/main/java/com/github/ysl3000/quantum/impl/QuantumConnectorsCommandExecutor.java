package com.github.ysl3000.quantum.impl;

import com.github.ysl3000.quantum.QuantumConnectors;
import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import com.github.ysl3000.quantum.api.receiver.AbstractReceiver;
import com.github.ysl3000.quantum.api.receiver.CompatReceiver;
import com.github.ysl3000.quantum.impl.commands.AbstractCommand;
import com.github.ysl3000.quantum.impl.commands.Help;
import com.github.ysl3000.quantum.impl.interfaces.ICircuitActivator;
import com.github.ysl3000.quantum.impl.utils.MessageLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class QuantumConnectorsCommandExecutor implements CommandExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuantumConnectorsCommandExecutor.class);
    private static final Pattern NUMBER = Pattern.compile("\\d+");
    private static final String CIRCUIT = "%circuit%";

    private QuantumConnectors plugin;
    private ICircuitActivator circuitManager;
    private MessageLogger messageLogger;

    private AbstractCommand abstractCommand = new AbstractCommand();

    public QuantumConnectorsCommandExecutor(QuantumConnectors plugin, ICircuitActivator circuitManager, MessageLogger messageLogger) {
        this.plugin = plugin;
        this.circuitManager = circuitManager;
        this.messageLogger = messageLogger;

        abstractCommand.registerCommand(new Help(messageLogger,circuitManager));

    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {

        boolean success = abstractCommand.onCommand(cs,args);

        if (!(cs instanceof Player)) {
            messageLogger.log(messageLogger.getMessage("console_not_allowed"));
            return true;
        }

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("q") || args[0].equalsIgnoreCase("normal"))
                args[0] = "quantum";
            else if (args[0].equalsIgnoreCase("t")) args[0] = "toggle";
            else if (args[0].equalsIgnoreCase("r")) args[0] = "reverse";
        }

        Player player = (Player) cs;

// Command was: "/qc"
        // todo map help




// Command was: "/qc cancel" or "/qc abort"
         if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("abort")) {

            //Pending circuit exists
            if (circuitManager.hasPendingCircuit(player)) {

                circuitManager.removePendingCircuit(player);

                messageLogger.msg(player, messageLogger.getMessage("cancelled"));

            }
            //No pending circuit
            else {
                messageLogger.msg(player, messageLogger.getMessage("no_pending_circuit"));
            }
        }

// Command was: "/qc done"
        else if (args[0].equalsIgnoreCase("done")) {

            //They typed "/qc <circuit>"
            if (circuitManager.hasPendingCircuit(player)) {
                AbstractCircuit pc = circuitManager.getPendingCircuit(player);
                //They also setup a sender
                if (pc.getLocation() != null) {
                    //Finally, they also setup at least one receiver
                    if (!pc.getReceivers().isEmpty()) {
                        circuitManager.addCircuit(pc);

                        circuitManager.removePendingCircuit(player);

                        messageLogger.msg(player, messageLogger.getMessage("circuit_created")
                                .replace(CIRCUIT, pc.getType()));
                    }
                    //They have not setup at least one receiver
                    else {
                        messageLogger.msg(player, messageLogger.getMessage("no_receivers"));
                    }
                }
                //They didn't setup a sender
                else {
                    messageLogger.msg(player, messageLogger.getMessage("no_sender"));
                }
            } else {
                messageLogger.msg(player, messageLogger.getMessage("no_pending_action"));
            }
        }

// Command was: "/qc <valid circuit type>"
        else if (circuitManager.isValidCircuitType(args[0])) {

            //Player has permission to create the circuit
            if (player.hasPermission("QuantumConnectors.create." + args[0])) {


                //Figure out if there's a delay, or use 0 for no delay
                int delay = 0;

                if (args.length > 1) {


                    if(NUMBER.matcher(args[1]).matches()){
                        delay = Integer.valueOf(args[1]);
                    }


                    if (delay < 0
                            || (delay > plugin.getMaxDelayTime() && !player.hasPermission("QuantumConnectors.ignoreLimits"))) {
                        delay = 0;

                        messageLogger.msg(player, ChatColor.RED +
                                messageLogger.getMessage("invalid_delay").
                                        replaceAll("%maxdelay%", Integer.toString(plugin.getMaxDelayTime())));
                    }
                }

                AbstractCircuit pendingCircuit = null;

                List<AbstractReceiver> validReceivers = new ArrayList<>();
                List<CompatReceiver> invalidReceiver = new ArrayList<>();

                int lastDelay = 0;

                if (circuitManager.hasPendingCircuit(player)) {
                    pendingCircuit = circuitManager.getPendingCircuit(player);

                    validReceivers = pendingCircuit.getReceivers();
                    invalidReceiver = pendingCircuit.getInValidReceivers();
                    lastDelay = pendingCircuit.getDelay();

                    circuitManager.removePendingCircuit(player);
                }

                if (delay != 0) {
                    lastDelay = delay;
                }

                try {
                    AbstractCircuit abstractCircuit = circuitManager.addPendingCircuit(
                            player, args[0], lastDelay);

                    abstractCircuit.addReceiver(validReceivers);
                    abstractCircuit.addInvalidReceiver(invalidReceiver);

                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    LOGGER.error("Circuit couldn't be initialized.", e);

                } finally {

                    if (pendingCircuit == null) {

                        messageLogger.msg(player, messageLogger.getMessage("circuit_ready")
                                .replace(CIRCUIT, args[0].toUpperCase())
                                .replace("%delay%", Integer.toString(delay)));
                    } else {
                        messageLogger.msg(player, messageLogger.getMessage("circuit_changed")
                                .replace(CIRCUIT, args[0].toUpperCase())
                                .replace("%delay%", Long.toString(delay)));
                    }
                }
            }

            //Player doesn't have permission
            else {
                messageLogger.msg(player, ChatColor.RED + messageLogger.getMessage("no_permission").replace("%circuit", args[0].toUpperCase()));
            }
        }

// Command was invalid
        else {
            messageLogger.msg(player, messageLogger.getMessage("invalid_circuit"));
        }

        return true;

    }//End onCommand
}
