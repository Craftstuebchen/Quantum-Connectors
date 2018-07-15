package com.github.ysl3000.quantum.impl.listeners;

import com.github.ysl3000.quantum.impl.interfaces.ICircuitLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class QuantumConnectorsWorldListener implements Listener {
    private ICircuitLoader circuitLoader;

    public QuantumConnectorsWorldListener(ICircuitLoader circuitLoader) {
        this.circuitLoader = circuitLoader;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldLoad(WorldLoadEvent event) {
        circuitLoader.loadWorld(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        circuitLoader.saveWorld(event.getWorld());
    }
}
