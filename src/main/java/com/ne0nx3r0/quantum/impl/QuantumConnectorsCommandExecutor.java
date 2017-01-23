package com.ne0nx3r0.quantum.impl;

import com.ne0nx3r0.quantum.QuantumConnectors;
import com.ne0nx3r0.quantum.impl.circuits.Circuit;
import com.ne0nx3r0.quantum.impl.circuits.CircuitManager;
import com.ne0nx3r0.quantum.impl.circuits.CircuitType;
import com.ne0nx3r0.quantum.impl.utils.MessageLogger;
import com.ne0nx3r0.quantum.impl.utils.Normalizer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class QuantumConnectorsCommandExecutor implements CommandExecutor {
    private QuantumConnectors plugin;
    private CircuitManager circuitManager;
    private MessageLogger messageLogger;

    public QuantumConnectorsCommandExecutor(QuantumConnectors plugin, CircuitManager circuitManager, MessageLogger messageLogger) {
        this.plugin = plugin;
        this.circuitManager = circuitManager;
        this.messageLogger = messageLogger;

    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
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
        if (args.length == 0 || args[0].equalsIgnoreCase("?")) {
            messageLogger.msg(player, messageLogger.getMessage("usage"));

            messageLogger.msg(player, ChatColor.YELLOW +
                    messageLogger.getMessage("available_circuits") + ChatColor.WHITE +
                    Normalizer.normalizeEnumNames(Arrays.asList(CircuitType.values()), Normalizer.NORMALIZER));
        }

// Command was: "/qc cancel" or "/qc abort"
        else if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("abort")) {

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
                Circuit pc = circuitManager.getPendingCircuit(player);
                //They also setup a sender
                if (pc.getLocation() != null) {
                    //Finally, they also setup at least one receiver
                    if (!pc.getReceivers().isEmpty()) {
                        circuitManager.addCircuit(pc);


                        /*
                        // I hate doors, I hate all the wooden doors.
                        // I just want to break them all, but I can't
                        // Can't break all wood doors.
                        if (pc.getLocation().getBlock().getType() == Material.WOODEN_DOOR
                                || pc.getLocation().getBlock().getType() == Material.SPRUCE_DOOR
                                || pc.getLocation().getBlock().getType() == Material.BIRCH_DOOR
                                || pc.getLocation().getBlock().getType() == Material.JUNGLE_DOOR
                                || pc.getLocation().getBlock().getType() == Material.ACACIA_DOOR
                                || pc.getLocation().getBlock().getType() == Material.DARK_OAK_DOOR) {

                            Block bDoor = pc.getLocation().getBlock();
                            int iData = (int) bDoor.getData();
                            Block bOtherPiece = bDoor.getRelative((iData & 0x08) == 0x08 ? BlockFace.DOWN : BlockFace.UP);

                            //TODO: Clone instead of reference the circuit?
                            //TODO: On break check if the circuit has a twin
                            circuitManager.addCircuit(bOtherPiece.getLocation(), pc);


                    }
                     */


                        circuitManager.removePendingCircuit(player);


                        messageLogger.msg(player, messageLogger.getMessage("circuit_created")
                                .replace("%circuit%", pc.getCircuitType().name));
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
                    try {
                        delay = Integer.valueOf(args[1]);
                    } catch (NumberFormatException e) {
                    }

                    if (delay < 0
                            || (delay > QuantumConnectors.MAX_DELAY_TIME && !player.hasPermission("QuantumConnectors.ignoreLimits"))) {
                        delay = 0;

                        messageLogger.msg(player, ChatColor.RED +
                                messageLogger.getMessage("invalid_delay").
                                        replaceAll("%maxdelay%", Integer.toString(QuantumConnectors.MAX_DELAY_TIME)));
                    }
                }

                String sDelayMsg = " (" + args[0] + " " + delay + "s delay)";


                if (!circuitManager.hasPendingCircuit(player)) {
                    circuitManager.addPendingCircuit(
                            player,
                            circuitManager.getCircuitType(args[0]), delay);

                    messageLogger.msg(player, messageLogger.getMessage("circuit_ready")
                            .replace("%circuit%", args[0].toUpperCase())
                            .replace("%delay%", Double.toString(delay)));
                } else {

                    Circuit pendingCircuit = circuitManager.getPendingCircuit(player);
                    pendingCircuit.setCircuitType(circuitManager.getCircuitType(args[0]));
                    pendingCircuit.setDelay(delay);

                    messageLogger.msg(player, messageLogger.getMessage("circuit_changed")
                            .replace("%circuit%", args[0].toUpperCase())
                            .replace("%delay%", Long.toString(delay)));
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