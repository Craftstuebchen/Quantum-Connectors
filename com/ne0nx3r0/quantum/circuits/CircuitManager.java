package com.ne0nx3r0.quantum.circuits;

import com.ne0nx3r0.quantum.QuantumConnectors;
import java.io.File;
import java.io.IOException;
import java.util.*;
import net.minecraft.server.EntityTNTPrimed;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftWorld;

public class CircuitManager{
    private final QuantumConnectors plugin;
    
    private FileConfiguration yml;
    private File ymlfile;
    
    //Lookup/Storage for circuits, and subsequently their receivers
    private static Map<Location, Circuit> circuits = new HashMap<Location, Circuit>();
    
    private Material[] validSenders = new Material[]{
        Material.LEVER,
        Material.REDSTONE_WIRE,
        Material.STONE_BUTTON,
        Material.STONE_PLATE,
        Material.WOOD_PLATE,
        Material.REDSTONE_TORCH_OFF,
        Material.REDSTONE_TORCH_ON,
        Material.IRON_DOOR_BLOCK,
        Material.WOODEN_DOOR,
        Material.TRAP_DOOR,
        Material.POWERED_RAIL,
    };
    private Material[] validReceivers = new Material[]{
        Material.LEVER,
        Material.IRON_DOOR_BLOCK,
        Material.WOODEN_DOOR,
        Material.TRAP_DOOR,
        Material.POWERED_RAIL,
        //Material.PISTON_BASE,
        //Material.PISTON_STICKY_BASE,
        //TODO: Add redstone lamps
        Material.TNT
    };
    
// Main
    public CircuitManager(File ymlFile,final QuantumConnectors qc){
        this.plugin = qc;
        
        if(!ymlFile.exists()) {
            try {
                ymlFile.createNewFile();
            } catch(IOException ex) {
                plugin.error("Could not create "+ymlFile.getName());
            }
        }
        
        ymlfile = ymlFile;
        yml = YamlConfiguration.loadConfiguration(ymlFile);
        
        this.load();
    }
    
// Persistence 
    public void save(){
        plugin.log("Saving "+this.ymlfile.getName()+"...");
        
        List<Object> tempCircuits = new ArrayList<Object>();
        
        Map<String,Object> tempCircuitObj;
        Map<String,Object> tempReceiverObj;
        ArrayList tempReceiverObjs;
        Circuit currentCircuit;
        Map<Location,Integer> currentRecivers;

        for(Location cLoc : circuits.keySet()){
            
            currentCircuit = circuits.get(cLoc);

            tempCircuitObj = new HashMap<String,Object>();
            
            tempCircuitObj.put("w",cLoc.getWorld().getName());
            tempCircuitObj.put("x",cLoc.getBlockX());
            tempCircuitObj.put("y",cLoc.getBlockY());
            tempCircuitObj.put("z",cLoc.getBlockZ());
            
            currentRecivers = currentCircuit.getReceivers();

            tempReceiverObjs = new ArrayList();
            for(Location rLoc : currentRecivers.keySet()){
                tempReceiverObj = new HashMap<String,Object>();
                
                tempReceiverObj.put("w",rLoc.getWorld().getName());
                tempReceiverObj.put("x",rLoc.getBlockX());
                tempReceiverObj.put("y",rLoc.getBlockY());
                tempReceiverObj.put("z",rLoc.getBlockZ());
                
                tempReceiverObj.put("t",currentRecivers.get(rLoc).intValue());
                
                tempReceiverObjs.add(tempReceiverObj);
            }

            tempCircuitObj.put("r",tempReceiverObjs);
            
            tempCircuits.add(tempCircuitObj);
        }
        
        yml.set("circuits", tempCircuits);
        yml.set("fileVersion","2");
        
        try {
            yml.save(ymlfile);
            
            plugin.log(this.ymlfile.getName()+" Saved!");
        } catch(IOException IO) {
            plugin.error("Failed to save "+this.ymlfile.getName());
        }        
    }
    
    public void load(){
        plugin.log("Loading "+this.ymlfile.getName()+"...");

        List<Map<String,Object>> tempCircuits = (List<Map<String,Object>>) yml.get("circuits");

        if(tempCircuits == null){
            plugin.log("No circuits found in "+this.ymlfile.getName());
            return;
        }
        
        Map<String,Object> tempReceiverObj;
        ArrayList tempReceiverObjs = null;

        Circuit tempCircuit = null;
        for(Map<String,Object> tempCircuitObj : tempCircuits){
            tempCircuit = new Circuit();
            tempReceiverObjs = (ArrayList) tempCircuitObj.get("r");
            
        //TODO: Better Location verification
        // For now we just make sure the sender & receiver worlds are valid
            //if(plugin.getServer().getWorld((String) tempCircuitObj.get("w")) != null){
                for(int i = 0; i < tempReceiverObjs.size(); i++) {
                    tempReceiverObj = (Map<String, Object>) tempReceiverObjs.get(i);

                //Make sure the receiver too has a valid world
                    //if(plugin.getServer().getWorld((String) tempReceiverObj.get("w")) != null){
                        tempCircuit.addReceiver(new Location(
                            plugin.getServer().getWorld((String) tempReceiverObj.get("w")),
                            (Integer) tempReceiverObj.get("x"),
                            (Integer) tempReceiverObj.get("y"),
                            (Integer) tempReceiverObj.get("z")),
                            (Integer) tempReceiverObj.get("t"));
                    //}
                    
                //Receiver was did not validate
                    //else{
                    //    plugin.log("Removed a '"+(String) tempReceiverObj.get("w")+" circuit: world doesn't exist");
                    //}
                }

            // Verify there is at least one valid receiver
                if(!tempCircuit.getReceivers().isEmpty()){
                    circuits.put(new Location(
                            plugin.getServer().getWorld((String) tempCircuitObj.get("w")),
                            (Integer) tempCircuitObj.get("x"),
                            (Integer) tempCircuitObj.get("y"),
                            (Integer) tempCircuitObj.get("z")
                        ),tempCircuit); 
                }
            // No valid receivers for this circuit
                else{
                    plugin.log("Removed a '"+(String) tempCircuitObj.get("w")+"' circuit: no valid receivers.");
                }
            //}

        //Circuit world didn't exist
            //else{
            //    plugin.log("Removed a '"+(String) tempCircuitObj.get("w")+"' circuit: world doesn't exist");
            //}
        }
    }
    
// Sender/Receiver checks
    public boolean isValidSender(Block block) {
        Material mBlock = block.getType();
        for (int i = 0; i < validSenders.length; i++) {
            if (mBlock == validSenders[i]) {
                return true;
            }
        }

        return false;
    }

    public String getValidSendersString() {
        String msg = "";
        for (int i = 0; i < validSenders.length; i++) {
            msg += (i != 0 ? ", " : "") + validSenders[i].name().toLowerCase().replace("_", " ");
        }

        return msg;
    }

    public boolean isValidReceiver(Block block) {
        Material mBlock = block.getType();
        for (int i = 0; i < validReceivers.length; i++) {
            if (mBlock == validReceivers[i]) {
                return true;
            }
        }

        return false;
    }

    public String getValidReceiversString() {
        String msg = "";
        for (int i = 0; i < validReceivers.length; i++) {
            msg += (i != 0 ? ", " : "") + validReceivers[i].name().toLowerCase().replace("_", " ");
        }

        return msg;
    }
    
    
// Circuit (sender) CRUD
    public void addCircuit(Location circuitLocation, Circuit newCircuit){
        //Notably circuits are now created from a temporary copy, rather than piecemeal here. 
        circuits.put(circuitLocation, newCircuit);
    }
    
    public boolean circuitExists(Location circuitLocation){
        return circuits.containsKey(circuitLocation);
    } 
    
    public Circuit getCircuit(Location circuitLocation){
        return circuits.get(circuitLocation);
    }
    
    public void removeCircuit(Location circuitLocation) {
        if(circuitExists(circuitLocation)){
            circuits.remove(circuitLocation);
        }
    }
    
// Circuit activation    
    public void activateCircuit(Location lSender, int current){
        activateCircuit(lSender, current, 0);
    }
    public void activateCircuit(Location lSender, int current, int chain){
        Circuit circuit = getCircuit(lSender);
        Map<Location,Integer> receivers = circuit.getReceivers();
        
        if(!receivers.isEmpty()){
            int iType;
            
            for(Location r : receivers.keySet()){
                iType = receivers.get(r).intValue();

                Block b = r.getBlock();

                if (isValidReceiver(b)) {
                    if (iType == CircuitTypes.QUANTUM.getId()) {
                        setReceiver(b, current > 0 ? true : false);
                    } else if (iType == CircuitTypes.ON.getId()) {
                        if (current > 0) {
                            setReceiver(b, true);
                        }
                    } else if (iType == CircuitTypes.OFF.getId()) {
                        if (current > 0) {
                            setReceiver(b, false);
                        }
                    } else if (iType == CircuitTypes.TOGGLE.getId()) {
                        if (current > 0) {
                            setReceiver(b, getBlockCurrent(b) > 0 ? false : true);
                        }
                    } else if (iType == CircuitTypes.REVERSE.getId()) {
                        setReceiver(b, current > 0 ? false : true);
                    } else if (iType == CircuitTypes.RANDOM.getId()) {
                        if (current > 0) {
                            setReceiver(b, new Random().nextBoolean() ? true : false);
                        }
                    }

                    if (b.getType() == Material.TNT) { // TnT is one time use!
                        circuit.delReceiver(r);
                    }

                    if (plugin.MAX_CHAIN_LINKS > 0) { //allow zero to be infinite
                        chain++;
                    }
                    if (chain <= plugin.MAX_CHAIN_LINKS && circuitExists(b.getLocation())) {
                        activateCircuit(b.getLocation(), getBlockCurrent(b), chain);
                    }
                }else{
                    circuit.delReceiver(r);
                }
            }
        } 
    }
        
    public int getBlockCurrent(Block b) {
        Material mBlock = b.getType();
        int iData = (int) b.getData();

        if (mBlock == Material.LEVER
                || mBlock == Material.POWERED_RAIL) {
            return (iData & 0x08) == 0x08 ? 15 : 0;
        } else if (mBlock == Material.IRON_DOOR_BLOCK
                || mBlock == Material.WOODEN_DOOR
                || mBlock == Material.TRAP_DOOR) {
            return (iData & 0x04) == 0x04 ? 15 : 0;
        }

        return b.getBlockPower();
    }

    private void setReceiver(Block block, boolean on) {
        Material mBlock = block.getType();
        int iData = (int) block.getData();

        if (mBlock == Material.LEVER) {
            if (on && (iData & 0x08) != 0x08) { // Massive annoyance
                iData |= 0x08; //send power on
            } else if (!on && (iData & 0x08) == 0x08) {
                iData ^= 0x08; //send power off
            }
            int i1 = iData & 7;
            net.minecraft.server.World w = ((net.minecraft.server.World) ((CraftWorld) block.getWorld()).getHandle());
            Location l = block.getLocation();
            int i = (int) l.getX();
            int j = (int) l.getY();
            int k = (int) l.getZ();
            int id = block.getTypeId();
            w.setData(i, j, k, iData);
            w.applyPhysics(i, j, k, id);
            if (i1 == 1) {
                w.applyPhysics(i - 1, j, k, id);
            } else if (i1 == 2) {
                w.applyPhysics(i + 1, j, k, id);
            } else if (i1 == 3) {
                w.applyPhysics(i, j, k - 1, id);
            } else if (i1 == 4) {
                w.applyPhysics(i, j, k + 1, id);
            } else {
                w.applyPhysics(i, j - 1, k, id);
            }
        } else if (mBlock == Material.POWERED_RAIL) {
            if (on && (iData & 0x08) != 0x08) {
                iData |= 0x08; //send power on
            } else if (!on && (iData & 0x08) == 0x08) {
                iData ^= 0x08; //send power off
            }
            block.setData((byte) iData);
        } else if (mBlock == Material.IRON_DOOR_BLOCK || mBlock == Material.WOODEN_DOOR) {
            Block bOtherPiece = block.getRelative(((iData & 0x08) == 0x08) ? BlockFace.DOWN : BlockFace.UP);
            int iOtherPieceData = (int) bOtherPiece.getData();

            if (on && (iData & 0x04) != 0x04) {
                iData |= 0x04;
                iOtherPieceData |= 0x04;
            } else if (!on && (iData & 0x04) == 0x04) {
                iData ^= 0x04;
                iOtherPieceData ^= 0x04;
            }
            block.setData((byte) iData);
            bOtherPiece.setData((byte) iOtherPieceData);
            block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0, 10);
        } else if (mBlock == Material.TRAP_DOOR) {
            if (on && (iData & 0x04) != 0x04) {
                iData |= 0x04;//send open
            } else if (!on && (iData & 0x04) == 0x04) {
                iData ^= 0x04;//send close
            }
            block.setData((byte) iData);
        } else if (mBlock == Material.TNT) {
            block.setType(Material.AIR);
            CraftWorld world = (CraftWorld) block.getWorld();
            EntityTNTPrimed tnt = new EntityTNTPrimed(world.getHandle(), block.getX() + 0.5F, block.getY() + 0.5F, block.getZ() + 0.5F);
            world.getHandle().addEntity(tnt);
            block.getWorld().playEffect(block.getLocation(), Effect.SMOKE, 1);
        } else if (mBlock == Material.PISTON_BASE || mBlock == Material.PISTON_STICKY_BASE) {
            // Makeshift piston code... Doesn't work!
            if (on && (iData & 0x08) != 0x08) {
                iData |= 0x08; //send power on
            } else if (!on && (iData & 0x08) == 0x08) {
                iData ^= 0x08; //send power off
            }
            block.setData((byte) iData);
            //net.minecraft.server.Block.PISTON.doPhysics(((CraftWorld)block.getWorld()).getHandle(), block.getX(), block.getY(), block.getZ(), -1);
        } else if (mBlock == Material.REDSTONE_TORCH_ON) {
            if (!on) {
                block.setType(Material.REDSTONE_TORCH_OFF);
            }
        } else if (mBlock == Material.REDSTONE_TORCH_OFF) {
            if (on) {
                block.setType(Material.REDSTONE_TORCH_ON);
            }
        }
    }
}
