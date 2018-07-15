package com.github.ysl3000.quantum.api.receiver;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.*;

public enum ReceiverState {
    S0(GRAY_WOOL, ACACIA_PLANKS),
    S1(GREEN_WOOL, BIRCH_PLANKS),
    S2(LIGHT_BLUE_WOOL, DARK_OAK_PLANKS),
    S3(LIGHT_GRAY_WOOL, JUNGLE_PLANKS),
    S4(BLUE_WOOL, OAK_PLANKS),

    S5(LIME_WOOL, SPRUCE_PLANKS),
    S6(MAGENTA_WOOL, SPRUCE_PLANKS),
    S7(ORANGE_WOOL, SPRUCE_PLANKS),
    S8(PINK_WOOL, SPRUCE_PLANKS),
    S9(PURPLE_WOOL, SPRUCE_PLANKS),
    S10(RED_WOOL, SPRUCE_PLANKS),
    S11(WHITE_WOOL, SPRUCE_PLANKS),
    S12(YELLOW_WOOL, SPRUCE_PLANKS),
    S13(BLACK_WOOL, SPRUCE_PLANKS),
    S14(BROWN_WOOL, SPRUCE_PLANKS),
    S15(CYAN_WOOL, SPRUCE_PLANKS);


    public final Material wool;
    public final Material plank;


    private static final Map<String, ReceiverState> BY_NAME = new HashMap<>();
    private static final Map<Material, ReceiverState> BY_WOOL = new HashMap<>();
    private static final Map<Material, ReceiverState> BY_PLANKS = new HashMap<>();

    static {
        for (ReceiverState state : values()) {
            BY_NAME.put(state.name(), state);
            BY_WOOL.put(state.wool, state);
            BY_PLANKS.put(state.plank, state);
        }


    }

    ReceiverState(Material wool, Material plank) {
        this.wool = wool;
        this.plank = plank;
    }

    public static ReceiverState getByName(String name) {
        return BY_NAME.get(name);
    }


    public static ReceiverState getByWool(Material wool) {
        return BY_WOOL.getOrDefault(wool, ReceiverState.S0);
    }

    public static ReceiverState getByPlanks(Material plank) {
        return BY_PLANKS.getOrDefault(plank, ReceiverState.S0);
    }

    public ReceiverState getOpposite() {
        return values()[(values().length - 1) - ordinal()];
    }

}
