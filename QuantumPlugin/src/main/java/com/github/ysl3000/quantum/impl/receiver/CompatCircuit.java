package com.github.ysl3000.quantum.impl.receiver;

import com.github.ysl3000.quantum.api.circuit.Circuit;
import com.github.ysl3000.quantum.api.receiver.AbstractReceiver;
import com.github.ysl3000.quantum.api.receiver.CompatReceiver;
import com.github.ysl3000.quantum.api.receiver.Receiver;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Yannick on 24.01.2017.
 */
public final class CompatCircuit implements Circuit {

    private Map<String, Object> circuit;


    public CompatCircuit(Map<String, Object> map) {
        this.circuit = map;
    }


    @Override
    public List<Material> getValidMaterials() {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> serialize() {
        return circuit;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void addReceiver(Class<? extends AbstractReceiver> receiverClass, Location loc, int delay) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addReceiver(AbstractReceiver receiver) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addReceiver(Collection<AbstractReceiver> receivers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addReceiver(AbstractReceiver... receivers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AbstractReceiver> getReceivers() {
        return new ArrayList<>();
    }

    @Override
    public int getReceiversCount() {
        return 0;
    }

    @Override
    public List<CompatReceiver> getInValidReceivers() {
        return new ArrayList<>();
    }

    @Override
    public int getWholeReceiverCount() {
        return 0;
    }

    @Override
    public int getInValidReceiversCount() {
        return 0;
    }

    @Override
    public void delReceiver(Receiver r) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delReceiver(Location location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReceiver(Location location) {
        return false;
    }

    @Override
    public UUID getOwner() {
        return null;
    }

    @Override
    public void setOwner(UUID playerUUID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public int getBlockCurrent() {
        return 0;
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void setLocation(Location location) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public void setDelay(int delay) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void calculate(Receiver receiver, int oldCurrent, int newCurrent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void actvate(int oldCurrent, int newCurrent, int chain) {
        throw new UnsupportedOperationException();
    }
}
