package com.github.ysl3000.quantum.impl.interfaces;

import com.github.ysl3000.quantum.api.receiver.Receiver;
import org.bukkit.block.Block;

/**
 * Created by Yannick on 21.01.2017.
 */
public interface ReceiverSetter {
    void setReceiver(Receiver receiver, boolean power);

    int getBlockCurrent(Block block);
}
