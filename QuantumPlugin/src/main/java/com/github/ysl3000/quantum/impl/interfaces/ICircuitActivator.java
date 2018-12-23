package com.github.ysl3000.quantum.impl.interfaces;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * Created by ysl3000
 */
public interface ICircuitActivator extends ICircuitManager {

    boolean isValidReceiver(Block block);

    boolean isValidSender(Block block);

    String getValidSendersAsString();

    boolean shouldLeaveReceiverOn(Block block);

    void activateCircuit(Location lSender, int oldCurrent, int newCurrent);

    void activateCircuit(Location lSender, int oldCurrent, int newCurrent, int chain);

    boolean isValidCircuitType(String type);

}
