package com.github.ysl3000.quantum.impl.interfaces;

import org.bukkit.block.Block;

public interface ICircuitManager {

    boolean isValidReceiver(Block block);

    boolean isValidSender(Block block);


}
