package com.github.ysl3000.quantum.impl;

import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import com.google.common.collect.Maps;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CircuitContainer {

    private Map<String, AbstractCircuit> pendingCircuits = Maps.newHashMap();
    private Map<World, Map<Location, AbstractCircuit>> worlds = Maps.newHashMap();

    public void addCircuit(AbstractCircuit pc) {

        World world = pc.getLocation().getWorld();

        if (!worlds.containsKey(world)) {
            Map<Location, AbstractCircuit> worldCircuits = new HashMap<>();
            worlds.put(world, worldCircuits);
        }

        worlds.get(world).put(pc.getLocation(), pc);
    }

    public void removeCircuit(Location circuitLocation) {
        if (circuitExists(circuitLocation)) {
            worlds.get(circuitLocation.getWorld()).remove(circuitLocation);
        }
    }

    public Map<Location, AbstractCircuit> getCircuitsForWorld(World world) {
        return worlds.get(world);
    }

    public Set<World> getWorlds() {
        return worlds.keySet();
    }

    public int getCircuitCount(World world) {

        if (!hasCircuits(world)) return 0;

        return worlds.get(world).size();
    }


    public boolean hasCircuits(World world) {
        return worlds.containsKey(world);
    }


    public boolean circuitExists(Location circuitLocation) {
        if (!hasCircuits(circuitLocation.getWorld())) return false;
        return worlds.get(circuitLocation.getWorld()).containsKey(circuitLocation);
    }


    public void addPendingCircuit(OfflinePlayer player, AbstractCircuit pc) {
        pendingCircuits.put(player.getName(), pc);
    }


    public AbstractCircuit getPendingCircuit(Player player) {
        return pendingCircuits.get(player.getName());
    }

    public boolean hasPendingCircuit(Player player) {
        return pendingCircuits.containsKey(player.getName());
    }

    public void removePendingCircuit(Player player) {
        pendingCircuits.remove(player.getName());
    }

    public Set<Location> circuitLocations(World w) {
        return worlds.get(w).keySet();
    }


    public AbstractCircuit getCircuit(Location circuitLocation) {
        return worlds.get(circuitLocation.getWorld()).get(circuitLocation);
    }
}
