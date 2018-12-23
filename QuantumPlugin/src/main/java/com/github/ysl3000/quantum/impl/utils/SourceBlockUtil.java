package com.github.ysl3000.quantum.impl.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;


/**
 * Created by Yannick on 21.01.2017.
 */
public class SourceBlockUtil {

    public Location getSourceBlock(Location location) {

        Block block = location.getBlock();
        BlockData blockData = block.getState().getBlockData();
        if (blockData instanceof Door) {
            Door door = (Door) blockData;
            if (door.getHalf()== Bisected.Half.TOP) {
                return location.add(0, -1, 0);
            }
        } else if (blockData instanceof Bed) {
            Bed bed = (Bed) blockData;
            if (bed.getPart()== Bed.Part.HEAD) {
                return block.getRelative(bed.getFacing().getOppositeFace()).getLocation();
            }
        } else if (block.getState() instanceof Chest) {
            Chest chest = (Chest) block.getState();

            Inventory inventory = chest.getBlockInventory();

            if (inventory instanceof DoubleChestInventory) {
                return ((DoubleChestInventory) inventory).getLeftSide().getLocation();
            }
            return ((Chest) inventory.getHolder()).getLocation();

        }
        return location;
    }


}
