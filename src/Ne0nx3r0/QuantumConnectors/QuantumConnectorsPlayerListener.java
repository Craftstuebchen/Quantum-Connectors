package Ne0nx3r0.QuantumConnectors;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.Map;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;

public class QuantumConnectorsPlayerListener extends PlayerListener {
    private final QuantumConnectors plugin;

    public static Map<Player,Integer> pendingCircuits;
    public static Map<Player,Location> pendingSenders;

    public QuantumConnectorsPlayerListener(QuantumConnectors instance){
        this.plugin = instance;
        
        pendingCircuits = new HashMap<Player,Integer>();
        pendingSenders = new HashMap<Player,Location>();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.isCancelled()){
            return;
        }

        if(event.getItem() != null && event.getClickedBlock() != null
        && event.getItem().getType() == Material.REDSTONE
        && pendingCircuits.containsKey(event.getPlayer())){
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            if(!pendingSenders.containsKey(player)){
                if(plugin.circuits.isValidSender(block)){
                    if(plugin.circuits.circuitExists(block.getLocation())){
                        plugin.msg(player,"A circuit already exists here!");
                        return;
                    }

                    pendingSenders.put(player,event.getClickedBlock().getLocation());
                    
                    plugin.msg(player,"Sender saved!");

                    if(block.getType() == Material.REDSTONE_WIRE
                    || block.getType() == Material.REDSTONE_TORCH_OFF
                    || block.getType() == Material.REDSTONE_TORCH_ON
                    || block.getType() == Material.DIODE_BLOCK_OFF
                    || block.getType() == Material.DIODE_BLOCK_ON){
                        event.setCancelled(true);
                    }
                }else if(plugin.circuits.circuitExists(block.getLocation())){//remove a possibly leftover circuit
                    plugin.circuits.removeCircuit(block.getLocation());
                    plugin.msg(player,ChatColor.YELLOW+"An old circuit was here, now removed - try again!");
                }else {
                    plugin.msg(player,ChatColor.RED+"Invalid sender!");
                    plugin.msg(player,ChatColor.YELLOW+"Senders: "+ChatColor.WHITE+plugin.circuits.getValidSendersString());
                }
            }else{
                if(plugin.circuits.isValidReceiver(block)){
                    plugin.circuits.addCircuit(
                        pendingSenders.get(player),
                        event.getClickedBlock().getLocation(),
                        pendingCircuits.get(player)
                    );

                    pendingSenders.remove(player);
                    pendingCircuits.remove(player);

                    plugin.msg(player,"Quantum Connector created!");
                }else if(plugin.circuits.circuitExists(block.getLocation())){//remove a possibly leftover circuit
                    plugin.circuits.removeCircuit(block.getLocation());
                    plugin.msg(player,ChatColor.YELLOW+"An old circuit was here, now removed - try again!");
                }else{
                    plugin.msg(player,ChatColor.RED+"Invalid receiver!");
                    plugin.msg(player,ChatColor.YELLOW+"Receivers: "+ChatColor.WHITE+plugin.circuits.getValidReceiversString());
                }
            }
        }
        else if(event.getClickedBlock() != null
        && plugin.circuits.circuitExists(event.getClickedBlock().getLocation())){
            Block block = event.getClickedBlock();

            if(block.getType() == Material.WOODEN_DOOR || block.getType() == Material.TRAP_DOOR){
                int iCurrent = 0;

                int iData = (int) block.getData();
                if((iData&0x04) == 0x04){
                    iCurrent = 15;
                }

                plugin.activateCircuit(event.getClickedBlock().getLocation(),iCurrent);
            }
        }
    }
}