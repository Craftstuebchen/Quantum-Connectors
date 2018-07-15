package com.github.ysl3000.quantum.impl.circuits;

import com.github.ysl3000.quantum.QuantumConnectors;
import com.github.ysl3000.quantum.api.IRegistry;
import com.github.ysl3000.quantum.api.QuantumConnectorsAPI;
import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import com.github.ysl3000.quantum.api.receiver.AbstractKeepAliveReceiver;
import com.github.ysl3000.quantum.api.receiver.AbstractReceiver;
import com.github.ysl3000.quantum.impl.interfaces.ICircuitActivator;
import com.github.ysl3000.quantum.impl.interfaces.ICircuitManager;
import com.github.ysl3000.quantum.impl.receiver.base.DelayedCircuit;
import com.github.ysl3000.quantum.impl.utils.MessageLogger;
import com.github.ysl3000.quantum.impl.utils.Normalizer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CircuitManager implements ICircuitActivator {

    private MessageLogger messageLogger;
    private Map<String, AbstractCircuit> pendingCircuits;
    private QuantumConnectors plugin;
    private Map<World, Map<Location, AbstractCircuit>> worlds = new HashMap<>();
    private CircuitLoader circuitLoader;
    private IRegistry<AbstractCircuit> circuitIRegistry;
    private IRegistry<AbstractReceiver> receiverIRegistry;

    public CircuitManager(MessageLogger messageLogger, final QuantumConnectors qc, IRegistry<AbstractCircuit> circuitIRegistry, IRegistry<AbstractReceiver> receiverIRegistry) {
        this.messageLogger = messageLogger;
        this.plugin = qc;
        this.circuitIRegistry = circuitIRegistry;
        this.receiverIRegistry = receiverIRegistry;
        this.circuitLoader = new CircuitLoader(qc, worlds, this, messageLogger, circuitIRegistry);

        this.pendingCircuits = new HashMap<>();

        circuitLoader.loadWorlds();
    }

    @Override
    public boolean isValidReceiver(Block block) {
        return this.receiverIRegistry.isValid(block);
    }

    @Override
    public boolean isValidSender(Block block) {
        return this.circuitIRegistry.isValid(block);
    }

    @Override
    public String getValidSendersAsString() {
        return String.join(", ", circuitIRegistry.getNames());
    }

    @Override
    public boolean shouldLeaveReceiverOn(Block block) {
        return AbstractKeepAliveReceiver.keepAlives.contains(block);
    }

    @Override
    public String getValidSendersMaterialsAsString() {
        return getValidString(circuitIRegistry.getMaterials());
    }

    @Override
    public String getValidString(Set<Material> materials) {
        return String.join(", ", Normalizer.normalizeEnumNames(materials, Normalizer.NORMALIZER));
    }

    @Override
    public String getValidReceiversMaterialsAsString() {
        return getValidString(receiverIRegistry.getMaterials());
    }

    @Override
    public void addCircuit(AbstractCircuit pc) {
        worlds.get(pc.getLocation().getWorld())
                .put(pc.getLocation(), pc);
    }

    @Override
    public void removeCircuit(Location circuitLocation) {
        if (circuitExists(circuitLocation)) {
            worlds.get(circuitLocation.getWorld()).remove(circuitLocation);
        }
    }

    @Override
    public boolean circuitExists(Location circuitLocation) {
        return worlds.get(circuitLocation.getWorld()).containsKey(circuitLocation);
    }

    // TODO: 23.01.2017 try to remove magic numbers
    // Circuit activation
    @Override
    public void activateCircuit(Location lSender, int oldCurrent, int newCurrent) {
        activateCircuit(lSender, oldCurrent, newCurrent, 0);
    }

    // TODO: 23.01.2017 try to remove magic numbers
    @Override
    public void activateCircuit(Location lSender, int oldCurrent, int newCurrent, int chain) {
        AbstractCircuit circuit = getCircuit(lSender);

        if (circuit.getDelay() > 0) {
            new DelayedCircuit(plugin, circuit).actvate(oldCurrent, newCurrent, chain);
        } else {
            circuit.actvate(oldCurrent, newCurrent, chain);
        }
    }

    @Override
    public AbstractCircuit getCircuit(Location circuitLocation) {
        return worlds.get(circuitLocation.getWorld()).get(circuitLocation);
    }

    @Override
    public AbstractCircuit addPendingCircuit(Player player, String type, int delay) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Class<? extends AbstractCircuit> clazz = circuitIRegistry.getFromUniqueKey(type);
        if (clazz == null) return null;

        Constructor<? extends AbstractCircuit> constructor = clazz.getConstructor(UUID.class, Integer.class);

        if (constructor == null) return null;

        AbstractCircuit pc = constructor.newInstance(player.getUniqueId(), delay);
        pendingCircuits.put(player.getName(), pc);
        return pc;
    }

    @Override
    public AbstractCircuit getPendingCircuit(Player player) {
        return pendingCircuits.get(player.getName());
    }

    @Override
    public boolean hasPendingCircuit(Player player) {
        return pendingCircuits.containsKey(player.getName());
    }

    @Override
    public void removePendingCircuit(Player player) {
        pendingCircuits.remove(player.getName());
    }

    //Circuit Types
    @Override
    public boolean isValidCircuitType(String type) {
        return circuitIRegistry.getFromUniqueKey(type) != null;
    }

    @Override
    public CircuitLoader getCircuitLoader() {
        return circuitLoader;
    }

    @Override
    public Set<Location> circuitLocations(World w) {
        return worlds.get(w).keySet();
    }

}
