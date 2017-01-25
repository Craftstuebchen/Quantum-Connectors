package de.ysl3000.quantum.api;

import de.ysl3000.quantum.api.circuit.AbstractCircuit;
import de.ysl3000.quantum.api.receiver.AbstractReceiver;
import de.ysl3000.quantum.api.receiver.ReceiverState;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public interface IQuantumConnectorsAPI {

    IRegistry<AbstractReceiver> getReceiverRegistry();

    IRegistry<AbstractCircuit> getCircuitRegistry();

    Location getSourceBlock(Location location);

    void setStatic(World world, boolean isStatic);

    void setState(Block block, ReceiverState receiverState);

    ReceiverState getState(Block block);

    boolean circuitExists(Location location);

    void activateCircuit(Location location, int oldCurrent, int newCurrent, int chain);

    int getMaxChainLinks();
}
