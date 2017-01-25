package com.ne0nx3r0.quantum.impl.utils;

import com.ne0nx3r0.quantum.api.receiver.AbstractKeepAliveReceiver;
import com.ne0nx3r0.quantum.api.receiver.QuantumState;
import com.ne0nx3r0.quantum.api.util.ValidMaterials;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.*;

public class VariantWrapper {

    public void setState(Block block, QuantumState receiverState) {
        BlockState blockState = block.getState();
        MaterialData md = blockState.getData();
        if (md instanceof Colorable) {
            ((Colorable) md).setColor(DyeColor.values()[receiverState.ordinal()]);
        } else if (md instanceof Wood) {
            int biggestIndex = DyeColor.values().length - 1;
            ((Wood) md).setSpecies(receiverState.ordinal() > biggestIndex ?
                    TreeSpecies.values()[biggestIndex] :
                    TreeSpecies.values()[receiverState.ordinal()]);
        }
        blockState.setData(md);
        blockState.update();
    }

    public QuantumState getState(Block block) {
        Material material = block.getType();
        MaterialData md = block.getState().getData();
        if (md instanceof Redstone) {
            return ((Redstone) md).isPowered() ? QuantumState.S15 : QuantumState.S0;
        } else if (md instanceof Openable) {
            return ((Openable) md).isOpen() ? QuantumState.S15 : QuantumState.S0;
        } else if (ValidMaterials.LAMP.contains(material)) {
            return AbstractKeepAliveReceiver.keepAlives.contains(block) ? QuantumState.S15 : QuantumState.S0;
        } else if (md instanceof Colorable) {
            return QuantumState.getByDyeColor(((Colorable) md).getColor());
        }

        return QuantumState.values()[block.getBlockPower()];

    }
}
