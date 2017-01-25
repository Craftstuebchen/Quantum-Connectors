package com.github.ysl3000.quantum.impl.interfaces;

import org.bukkit.World;

/**
 * Created by ysl3000 on 19.01.17.
 */
public interface ICircuitLoader {
    void saveAllWorlds();

    void saveWorld(World world);


    void loadWorlds();

    void loadWorld(World world);


}
