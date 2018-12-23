package com.github.ysl3000.quantum.impl.circuits;

import com.github.ysl3000.quantum.QuantumConnectors;
import com.github.ysl3000.quantum.api.IRegistry;
import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import com.github.ysl3000.quantum.api.circuit.Circuit;
import com.github.ysl3000.quantum.impl.CircuitContainer;
import com.github.ysl3000.quantum.impl.interfaces.ICircuitLoader;
import com.github.ysl3000.quantum.impl.receiver.CompatCircuit;
import com.github.ysl3000.quantum.impl.utils.MessageLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CircuitLoader implements ICircuitLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitLoader.class);
    private static final String FILE = "%file%";

    private final IRegistry<AbstractCircuit> circuitIRegistry;
    private final CircuitContainer circuitContainer;
    private Map<String, List<Circuit>> invalidCicuitsWorld = new HashMap<>();
    private QuantumConnectors plugin;
    private MessageLogger messageLogger;

    public CircuitLoader(QuantumConnectors plugin, MessageLogger messageLogger, IRegistry<AbstractCircuit> circuitIRegistry, CircuitContainer circuitContainer) {
        this.plugin = plugin;
        this.circuitContainer = circuitContainer;
        this.messageLogger = messageLogger;
        this.circuitIRegistry = circuitIRegistry;
    }

    public void saveAllWorlds() {
        for (String world : circuitContainer.getWorlds()) {
            saveWorld(world);
        }
    }

    public void saveWorld(String worldName) {
        if (circuitContainer.hasCircuits(worldName)) {
            //Alright let's do this!
            File ymlFile = getFile(worldName);
            FileConfiguration yml = YamlConfiguration.loadConfiguration(ymlFile);

            messageLogger.verbose(messageLogger.getMessage("saving").replace("%file", ymlFile.getName()));

            Map<Location, AbstractCircuit> currentWorldCircuits = circuitContainer.getCircuitsForWorld(worldName);
            List<Circuit> currentInvalidCircuits = invalidCicuitsWorld.get(worldName);

            List<Map<String, Object>> mapList = new ArrayList<>();

            for (Map.Entry<Location, AbstractCircuit> entry : currentWorldCircuits.entrySet()) {
                mapList.add(entry.getValue().serialize());
            }
            for (Circuit invalidCircuit : currentInvalidCircuits) {
                mapList.add(invalidCircuit.serialize());
            }

            yml.set("fileVersion", "3");
            yml.set("circuits", mapList);

            try {
                yml.save(ymlFile);

                messageLogger.verbose(messageLogger.getMessage("saved").replace("%file", ymlFile.getName()));
            } catch (IOException e) {
                messageLogger.error(messageLogger.getMessage("save_failed").replace("%world", worldName));
            }
        } else {
            messageLogger.error(messageLogger.getMessage("save_failed").replace("%world", worldName));
        }
    }

    @Override
    public void loadWorlds() {
        for (String worldName : Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toSet())) {
            loadWorld(worldName);
        }
    }

    public void loadWorld(String worldName) {
        //at least create a blank holder


        List<Circuit> invalidCircuits = new ArrayList<>();
        invalidCicuitsWorld.put(worldName, invalidCircuits);

        File ymlFile = new File(plugin.getDataFolder(), worldName + ".circuits.yml");

        messageLogger.verbose(messageLogger.getMessage("loading").replace(FILE, ymlFile.getName()));

        if (!ymlFile.exists()) {
            messageLogger.verboseError(messageLogger.getMessage("loading_not_found").replace(FILE, ymlFile.getName()));
            return;
        }

        FileConfiguration yml = YamlConfiguration.loadConfiguration(ymlFile);

        List<Map<?, ?>> tempCircuits = yml.getMapList("circuits");


        if (tempCircuits.isEmpty()) {
            messageLogger.log(messageLogger.getMessage("loading_no_circuits").replace(FILE, ymlFile.getName()));
            return;
        }

        loadCircuits(tempCircuits, invalidCircuits);

        LOGGER.debug("Debug: Anzahl der geladenene Schaltungen in Welt {}: {}", worldName, circuitContainer.getCircuitCount(worldName));

    }

    private void loadCircuits(List<Map<?, ?>> tempCircuits, List<Circuit> invalidCircuits) {

        for (Map<?, ?> tempCircuitObj : tempCircuits) {

            Map<String, ?> tempCircuitMap = (Map<String, ?>) tempCircuitObj;

            String circuitType = (String) tempCircuitMap.get("type");

            try {

                Constructor<? extends AbstractCircuit> circuitConstructor = circuitIRegistry.getInstance(circuitType);
                if (circuitConstructor == null) {

                    Circuit circuit = new CompatCircuit((HashMap<String, Object>) tempCircuitMap);
                    invalidCircuits.add(circuit);

                    LOGGER.info("There is no receiver registered with this type: {}", circuitType);
                    continue;
                }

                AbstractCircuit receiver = circuitConstructor.newInstance(tempCircuitMap);

                if (receiver.isValid()) {
                    circuitContainer.addCircuit(receiver);
                }

            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                LOGGER.error("Circuit loading failed.", e);
            }
        }
    }

    private File getFile(String worldName) {
        File ymlFile = new File(plugin.getDataFolder(), worldName + ".circuits.yml");
        if (!ymlFile.exists()) {
            try {
                if (!ymlFile.createNewFile()) {
                    LOGGER.error("Could not create {}", ymlFile.getName());
                }
            } catch (IOException ex) {
                LOGGER.error("Could not access {}", ymlFile.getName());
            }
        }
        return ymlFile;
    }
}
