package com.github.ysl3000.quantum.impl.utils;

import com.github.ysl3000.quantum.api.receiver.ReceiverState;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class VariantWrapper {

    public void setState(Block block, ReceiverState receiverState) {
        BlockData blockData = block.getBlockData();
        trySet(blockData, Powerable.class, powerable -> powerable.setPowered(receiverState == ReceiverState.S15));
        trySet(blockData, org.bukkit.block.data.Openable.class, openable -> openable.setOpen(receiverState == ReceiverState.S15));

        if (Tag.WOOL.isTagged(block.getType())) {
            block.setType(receiverState.wool);
        } else if (Tag.PLANKS.isTagged(block.getType())) {
            block.setType(receiverState.plank);
        }
    }

    public ReceiverState getState(Block block) {
        BlockData blockData = block.getBlockData();

        Optional<ReceiverState> optionalReceiverState = tryCast(blockData, Powerable.class, powerable -> powerable.isPowered() ? ReceiverState.S15 : ReceiverState.S0);

        if (optionalReceiverState.isPresent()) {
            return optionalReceiverState.get();
        }

        optionalReceiverState = tryCast(blockData, org.bukkit.block.data.Openable.class, openable -> openable.isOpen() ? ReceiverState.S15 : ReceiverState.S0);

        if (optionalReceiverState.isPresent()) {
            return optionalReceiverState.get();
        }


        if (Tag.WOOL.isTagged(block.getType())) {
            return ReceiverState.getByWool(block.getType());
        }

        if (Tag.PLANKS.isTagged(block.getType())) {
            return ReceiverState.getByPlanks(block.getType());
        }
        return ReceiverState.values()[block.getBlockPower()];

    }


    private <T extends BlockData> void trySet(BlockData blockData, Class<T> clazz, Consumer<T> function) {
        if (blockData.getClass().isAssignableFrom(clazz)) {
            function.accept(clazz.cast(blockData));
        }
    }


    private <T extends BlockData> Optional<ReceiverState> tryCast(BlockData blockData, Class<T> clazz, Function<T, ReceiverState> function) {
        if (blockData.getClass().isAssignableFrom(clazz)) {
            return Optional.of(function.apply(clazz.cast(blockData)));
        }
        return Optional.empty();
    }
}
