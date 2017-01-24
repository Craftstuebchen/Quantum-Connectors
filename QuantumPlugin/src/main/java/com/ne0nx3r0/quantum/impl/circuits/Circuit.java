package com.ne0nx3r0.quantum.impl.circuits;

import com.ne0nx3r0.quantum.api.IRegistry;
import com.ne0nx3r0.quantum.api.QuantumConnectorsAPI;
import com.ne0nx3r0.quantum.api.receiver.AbstractReceiver;
import com.ne0nx3r0.quantum.api.receiver.Receiver;
import com.ne0nx3r0.quantum.impl.receiver.base.Registry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

//TODO: Allow naming of circuits

public class Circuit implements ConfigurationSerializable {

    private Map<Location, Receiver> receivers = new HashMap<>();
    private UUID playerUUID;
    private CircuitType circuitTypes;
    private Location location;
    private int delay;


    public Circuit(UUID playerUUID, CircuitType circuitTypes, int delay) {
        this.playerUUID = playerUUID;
        this.circuitTypes = circuitTypes;
        this.delay = delay;
    }

    public Circuit(Map<?, ?> map) {
        this.playerUUID = UUID.fromString((String) map.get("owner"));
        this.circuitTypes = CircuitType.getByName((String) map.get("type"));
        this.location = new Location(Bukkit.getWorld((String) map.get("world")), (Integer) map.get("x"), (Integer) map.get("y"), (Integer) map.get("z"));
        this.delay = (Integer) map.get("delay");
        List<?> reciverObjectList = (List<?>) map.get("receiver");

        for (Object receiverObject : reciverObjectList) {

            Map<?, ?> receiverMap = (Map<?, ?>) receiverObject;

            String type = (String) receiverMap.get("type");
            try {

                IRegistry<AbstractReceiver> receiverIRegistry = QuantumConnectorsAPI.getReceiverRegistry();

                Constructor<? extends AbstractReceiver> receiverConstructor = (receiverIRegistry instanceof Registry) ? ((Registry<AbstractReceiver>) receiverIRegistry).getInstance(type) : null;
                if (receiverConstructor == null) {
                    System.out.println("There is no receiver registered with this type: " + type);
                    continue;
                }

                Receiver receiver = receiverConstructor.newInstance(receiverMap);

                if (receiver.isValid()) {
                    receivers.put(receiver.getLocation(), receiver);
                }

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    public void addReceiver(Class<? extends AbstractReceiver> receiverClass, Location loc, int delay) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        IRegistry<AbstractReceiver> receiverIRegistry = QuantumConnectorsAPI.getReceiverRegistry();
        if (receiverIRegistry instanceof Registry)
            receivers.put(loc, ((Registry<AbstractReceiver>) receiverIRegistry).instantiateFrom(receiverClass, loc, delay));
    }

    public List<Receiver> getReceivers() {
        return new ArrayList<>(receivers.values());
    }

    public int getReceiversCount() {
        return receivers.size();
    }

    public void delReceiver(Receiver r) {
        delReceiver(r.getLocation());
    }

    public void delReceiver(Location location) {
        receivers.remove(location);
    }


    public boolean isReceiver(Location location) {
        return receivers.containsKey(location);
    }

    public UUID getOwner() {
        return playerUUID;
    }

    public void setOwner(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public boolean isValid() {
        // TODO: 20.01.2017 write validation
        return true;
    }

    public CircuitType getCircuitType() {
        return circuitTypes;
    }

    public void setCircuitType(CircuitType circuitType) {
        this.circuitTypes = circuitType;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("owner", playerUUID.toString());
        map.put("type", circuitTypes.name);

        List<Map<String, ?>> receiverMap = new ArrayList<>();

        for (Receiver receiver : receivers.values()) {
            receiverMap.add(receiver.serialize());
        }
        map.put("receiver", receiverMap);
        map.put("world", location.getWorld().getName());
        map.put("x", location.getBlockX());
        map.put("y", location.getBlockY());
        map.put("z", location.getBlockZ());
        map.put("delay", delay);
        return map;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}