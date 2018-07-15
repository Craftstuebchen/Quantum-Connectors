package com.github.ysl3000.quantum.impl.interfaces;

import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public interface ICircuitManager {

    String getValidSendersMaterialsAsString();

    String getValidString(Set<Material> materials);

    String getValidReceiversMaterialsAsString();

    void addCircuit(AbstractCircuit pc);

    void removeCircuit(Location circuitLocation);

    boolean circuitExists(Location circuitLocation);

    boolean isValidReceiver(Block block);

    boolean isValidSender(Block block);

    AbstractCircuit getCircuit(Location circuitLocation);

    Set<Location> circuitLocations(World w);

    AbstractCircuit addPendingCircuit(Player player, String type, int delay) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;

    AbstractCircuit getPendingCircuit(Player player);

    boolean hasPendingCircuit(Player player);

    void removePendingCircuit(Player player);
}
