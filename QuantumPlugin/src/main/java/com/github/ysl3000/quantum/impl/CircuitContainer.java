package com.github.ysl3000.quantum.impl;

import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import com.google.common.collect.Maps;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CircuitContainer {

    private Map<UUID, AbstractCircuit> pendingCircuits = Maps.newHashMap();
    private Map<String, Map<Location, AbstractCircuit>> worlds = Maps.newHashMap();

    public void addCircuit(AbstractCircuit pc) {

        String world = pc.getLocation().getWorld().getName();

        if (!worlds.containsKey(world)) {
            Map<Location, AbstractCircuit> worldCircuits = new HashMap<>();
            worlds.put(world, worldCircuits);
        }

        worlds.get(world).put(pc.getLocation(), pc);
    }

    public void removeCircuit(Location circuitLocation) {
        if (circuitExists(circuitLocation)) {
            worlds.get(circuitLocation.getWorld().getName()).remove(circuitLocation);
        }
    }

    public Map<Location, AbstractCircuit> getCircuitsForWorld(String worldName) {
        return worlds.get(worldName);
    }

    public Set<String> getWorlds() {
        return worlds.keySet();
    }

    public int getCircuitCount(String worldName) {

        if (!hasCircuits(worldName)) return 0;

        return worlds.get(worldName).size();
    }


    public boolean hasCircuits(String worldName) {
        return worlds.containsKey(worldName);
    }


    public boolean circuitExists(Location circuitLocation) {
        if (!hasCircuits(circuitLocation.getWorld().getName())) return false;
        return worlds.get(circuitLocation.getWorld().getName()).containsKey(circuitLocation);
    }


    public void addPendingCircuit(UUID playerUUID, AbstractCircuit pc) {
        pendingCircuits.put(playerUUID, pc);
    }


    public AbstractCircuit getPendingCircuit(UUID playerUUID) {
        return pendingCircuits.get(playerUUID);
    }

    public boolean hasPendingCircuit(UUID playerUUID) {
        return pendingCircuits.containsKey(playerUUID);
    }

    public void removePendingCircuit(UUID playerUUID) {
        pendingCircuits.remove(playerUUID);
    }

    public Set<Location> circuitLocations(String worldName) {
        return worlds.get(worldName).keySet();
    }


    public AbstractCircuit getCircuit(Location circuitLocation) {
        return worlds.get(circuitLocation.getWorld().getName()).get(circuitLocation);
    }
}
