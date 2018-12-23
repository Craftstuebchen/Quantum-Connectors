package com.github.ysl3000.quantum.impl.interfaces;

/**
 * Created by ysl3000 on 19.01.17.
 */
public interface ICircuitLoader {
    void saveAllWorlds();

    void saveWorld(String worldName);


    void loadWorlds();

    void loadWorld(String worldName);


}
