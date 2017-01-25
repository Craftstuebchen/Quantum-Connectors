package com.ne0nx3r0.quantum.api.receiver;

import org.bukkit.DyeColor;

import java.util.HashMap;
import java.util.Map;

public enum QuantumState {
    S0, S1, S2, S3, S4, S5, S6, S7, S8, S9, S10, S11, S12, S13, S14, S15;


    private static final Map<String, QuantumState> BY_NAME = new HashMap<>();

    static {
        for (QuantumState state : values())
            BY_NAME.put(state.name(), state);

    }

    public static QuantumState getByName(String name) {
        return BY_NAME.get(name);
    }

    public static QuantumState getByDyeColor(DyeColor dyeColor) {
        return QuantumState.values()[dyeColor.ordinal()];
    }

    public QuantumState getOpposite() {
        return values()[(values().length - 1) - ordinal()];
    }

}
