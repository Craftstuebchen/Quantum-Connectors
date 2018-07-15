package com.github.ysl3000.quantum.impl.interfaces;

import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by ysl3000
 */
public interface ICircuitActivator extends ICircuitManager{

    boolean isValidReceiver(Block block);

    boolean isValidSender(Block block);

    String getValidSendersAsString();

    boolean shouldLeaveReceiverOn(Block block);



    // TODO: 23.01.2017 try to remove magic numbers
    // Circuit activation
    void activateCircuit(Location lSender, int oldCurrent, int newCurrent);

    // TODO: 23.01.2017 try to remove magic numbers
    void activateCircuit(Location lSender, int oldCurrent, int newCurrent, int chain);

    //Circuit Types
    boolean isValidCircuitType(String type);

}
