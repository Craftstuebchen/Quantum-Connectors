package com.github.ysl3000.quantum.api;

import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bukkit.Material.*;

public class ValidCircuitMaterials {

    public final static Set<Material> WOODEN_FENCE_GATES = Stream.of(
            SPRUCE_FENCE_GATE,
            BIRCH_FENCE_GATE,
            JUNGLE_FENCE_GATE,
            ACACIA_FENCE_GATE,
            DARK_OAK_FENCE_GATE,
            OAK_FENCE_GATE
    ).collect(Collectors.toSet());


    public final static Set<Material> WOODEN_TRAPDOORS = Stream.of(
            ACACIA_TRAPDOOR,
            BIRCH_TRAPDOOR,
            DARK_OAK_TRAPDOOR,
            JUNGLE_TRAPDOOR,
            OAK_TRAPDOOR,
            SPRUCE_TRAPDOOR
    ).collect(Collectors.toSet());


    public final static Set<Material> BLOCK_COLLECTION = Stream.of(
            SPRUCE_FENCE_GATE,
            BIRCH_FENCE_GATE,
            JUNGLE_FENCE_GATE,
            ACACIA_FENCE_GATE,
            DARK_OAK_FENCE_GATE,
            CHEST,
            BOOKSHELF,
            FURNACE
    ).collect(Collectors.toSet());


    public final static Set<Material> BEDS = Stream.of(
            WHITE_BED,
            ORANGE_BED,
            MAGENTA_BED,
            LIGHT_BLUE_BED,
            YELLOW_BED,
            LIME_BED,
            PINK_BED,
            GRAY_BED,
            LIGHT_GRAY_BED,
            CYAN_BED,
            PURPLE_BED,
            BLUE_BED,
            BROWN_BED,
            GREEN_BED,
            RED_BED,
            BLACK_BED
    ).collect(Collectors.toSet());


    public final static Collection<Material> VALID_MATERIALS = Stream.of(
            Tag.BUTTONS.getValues(),
            Tag.WOODEN_BUTTONS.getValues(),
            Tag.WOODEN_PRESSURE_PLATES.getValues(),
            Tag.DOORS.getValues(),
            Tag.WOODEN_DOORS.getValues(),
            BEDS,
            BLOCK_COLLECTION,
            WOODEN_TRAPDOORS,
            WOODEN_FENCE_GATES,
            Arrays.asList(
                    STONE_PRESSURE_PLATE,
                    LIGHT_WEIGHTED_PRESSURE_PLATE,
                    HEAVY_WEIGHTED_PRESSURE_PLATE,
                    LEVER,
                    REDSTONE_LAMP,
                    REDSTONE_WIRE,
                    IRON_DOOR,
                    IRON_TRAPDOOR)
    ).flatMap(Collection::stream).collect(Collectors.toSet());


}
