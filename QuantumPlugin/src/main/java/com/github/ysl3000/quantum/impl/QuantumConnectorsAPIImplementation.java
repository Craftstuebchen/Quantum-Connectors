package com.github.ysl3000.quantum.impl;

import com.github.ysl3000.quantum.api.IQuantumConnectorsAPI;
import com.github.ysl3000.quantum.api.IRegistry;
import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import com.github.ysl3000.quantum.api.receiver.AbstractReceiver;
import com.github.ysl3000.quantum.api.receiver.ReceiverState;
import com.github.ysl3000.quantum.impl.interfaces.ICircuitActivator;
import com.github.ysl3000.quantum.impl.nmswrapper.IQSWorld;
import com.github.ysl3000.quantum.impl.receiver.base.Registry;
import com.github.ysl3000.quantum.impl.utils.SourceBlockUtil;
import com.github.ysl3000.quantum.impl.utils.VariantWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class QuantumConnectorsAPIImplementation implements IQuantumConnectorsAPI {

    private final int maxChainLinks;
    private final ICircuitActivator circuitManager;
    private Registry<AbstractReceiver> receiverRegistry;
    private Registry<AbstractCircuit> circuitRegistry;
    private SourceBlockUtil sourceBlockUtil;
    private IQSWorld IQSWorld;
    private VariantWrapper variantWrapper;

    public QuantumConnectorsAPIImplementation(Registry<AbstractReceiver> receiverRegistry, Registry<AbstractCircuit> circuitRegistry, SourceBlockUtil sourceBlockUtil, IQSWorld IQSWorld, VariantWrapper variantWrapper, int maxChainLinks, ICircuitActivator circuitManager) {
        this.receiverRegistry = receiverRegistry;
        this.circuitRegistry = circuitRegistry;
        this.sourceBlockUtil = sourceBlockUtil;
        this.IQSWorld = IQSWorld;
        this.variantWrapper = variantWrapper;
        this.maxChainLinks = maxChainLinks;
        this.circuitManager = circuitManager;
    }

    @Override
    public IRegistry<AbstractReceiver> getReceiverRegistry() {
        return receiverRegistry;
    }

    @Override
    public IRegistry<AbstractCircuit> getCircuitRegistry() {
        return circuitRegistry;
    }

    @Override
    public Location getSourceBlock(Location location) {
        return sourceBlockUtil.getSourceBlock(location);
    }

    @Override
    public void setStatic(World world, boolean isStatic) {
        IQSWorld.setStatic(world, isStatic);
    }

    @Override
    public void setState(Block block, ReceiverState receiverState) {
        variantWrapper.setState(block, receiverState);
    }

    @Override
    public ReceiverState getState(Block block) {
        return variantWrapper.getState(block);
    }

    @Override
    public boolean circuitExists(Location location) {
        return circuitManager.circuitExists(location);
    }

    @Override
    public void activateCircuit(Location location, int oldCurrent, int newCurrent, int chain) {
        circuitManager.activateCircuit(location, oldCurrent, newCurrent, chain);
    }

    @Override
    public int getMaxChainLinks() {
        return maxChainLinks;
    }

    public void unregisterAll() {
        this.receiverRegistry.unregisterAll();
        this.circuitRegistry.unregisterAll();
    }
}
