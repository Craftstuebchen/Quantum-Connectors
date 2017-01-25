package de.ysl3000.quantum.impl.circuits;

import de.ysl3000.quantum.QuantumConnectors;
import de.ysl3000.quantum.api.IRegistry;
import de.ysl3000.quantum.api.QuantumConnectorsAPI;
import de.ysl3000.quantum.api.circuit.AbstractCircuit;
import de.ysl3000.quantum.api.receiver.AbstractKeepAliveReceiver;
import de.ysl3000.quantum.api.receiver.AbstractReceiver;
import de.ysl3000.quantum.impl.interfaces.ICircuitManager;
import de.ysl3000.quantum.impl.receiver.base.DelayedCircuit;
import de.ysl3000.quantum.impl.utils.MessageLogger;
import de.ysl3000.quantum.impl.utils.Normalizer;
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

public final class CircuitManager implements ICircuitManager {

    private MessageLogger messageLogger;
    // Temporary Holders for circuit creation
    private Map<String, AbstractCircuit> pendingCircuits;
    // Allow circuitTypes/circuits
    private QuantumConnectors plugin;
    // Lookup/Storage for circuits, and subsequently their receivers
    private Map<World, Map<Location, AbstractCircuit>> worlds = new HashMap<>();
    private CircuitLoader circuitLoader;
    private IRegistry<AbstractCircuit> circuitIRegistry;
    private IRegistry<AbstractReceiver> receiverIRegistry;

    // Main
    public CircuitManager(MessageLogger messageLogger, final QuantumConnectors qc, IRegistry<AbstractCircuit> circuitIRegistry, IRegistry<AbstractReceiver> receiverIRegistry) {
        this.messageLogger = messageLogger;
        this.plugin = qc;
        this.circuitIRegistry = circuitIRegistry;
        this.receiverIRegistry = receiverIRegistry;
        this.circuitLoader = new CircuitLoader(qc, worlds, this, messageLogger, circuitIRegistry);

        //Create a holder for pending circuits
        this.pendingCircuits = new HashMap<>();

        //Init any loaded worlds
        circuitLoader.loadWorlds();
    }

    public boolean isValidReceiver(Block block) {
        return QuantumConnectorsAPI.getReceiverRegistry().isValid(block);
    }

    public boolean isValidSender(Block block) {
        return this.circuitIRegistry.isValid(block);
    }

    public String getValidSendersAsString() {
        return String.join(", ", circuitIRegistry.getNames());
    }

    public boolean shouldLeaveReceiverOn(Block block) {
        return AbstractKeepAliveReceiver.keepAlives.contains(block);
    }

    public String getValidSendersMaterialsAsString() {
        return getValidString(circuitIRegistry.getMaterials());
    }

    private String getValidString(Set<Material> materials) {
        return String.join(", ", Normalizer.normalizeEnumNames(materials, Normalizer.NORMALIZER));
    }

    public String getValidReceiversMaterialsAsString() {
        return getValidString(receiverIRegistry.getMaterials());
    }

    public void addCircuit(AbstractCircuit pc) {
        worlds.get(pc.getLocation().getWorld())
                .put(pc.getLocation(), pc);
    }

    public void removeCircuit(Location circuitLocation) {
        if (circuitExists(circuitLocation)) {
            worlds.get(circuitLocation.getWorld()).remove(circuitLocation);
        }
    }

    public boolean circuitExists(Location circuitLocation) {
        return worlds.get(circuitLocation.getWorld()).containsKey(circuitLocation);
    }

    // TODO: 23.01.2017 try to remove magic numbers
    // Circuit activation
    public void activateCircuit(Location lSender, int oldCurrent, int newCurrent) {
        activateCircuit(lSender, oldCurrent, newCurrent, 0);
    }

    // TODO: 23.01.2017 try to remove magic numbers
    public void activateCircuit(Location lSender, int oldCurrent, int newCurrent, int chain) {
        AbstractCircuit circuit = getCircuit(lSender);

        if (circuit.getDelay() > 0) {
            new DelayedCircuit(plugin, circuit).actvate(oldCurrent, newCurrent, chain);
        } else {
            circuit.actvate(oldCurrent, newCurrent, chain);
        }
    }

    public AbstractCircuit getCircuit(Location circuitLocation) {
        return worlds.get(circuitLocation.getWorld()).get(circuitLocation);
    }

    public AbstractCircuit addPendingCircuit(Player player, String type, int delay) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Class<? extends AbstractCircuit> clazz = circuitIRegistry.getFromUniqueKey(type);
        if (clazz == null) return null;

        Constructor<? extends AbstractCircuit> constructor = clazz.getConstructor(UUID.class, Integer.class);

        if (constructor == null) return null;

        AbstractCircuit pc = constructor.newInstance(player.getUniqueId(), delay);
        pendingCircuits.put(player.getName(), pc);
        return pc;
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

    //Circuit Types
    public boolean isValidCircuitType(String type) {
        return circuitIRegistry.getFromUniqueKey(type) != null;
    }

    public CircuitLoader getCircuitLoader() {
        return circuitLoader;
    }

    public Set<Location> circuitLocations(World w) {
        return worlds.get(w).keySet();
    }

}
