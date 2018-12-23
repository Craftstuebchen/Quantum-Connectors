package com.github.ysl3000.quantum;

import com.github.ysl3000.quantum.api.QuantumConnectorsAPI;
import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import com.github.ysl3000.quantum.api.receiver.AbstractReceiver;
import com.github.ysl3000.quantum.impl.CircuitContainer;
import com.github.ysl3000.quantum.impl.QuantumConnectorsAPIImplementation;
import com.github.ysl3000.quantum.impl.QuantumConnectorsCommandExecutor;
import com.github.ysl3000.quantum.impl.circuits.CircuitLoader;
import com.github.ysl3000.quantum.impl.circuits.CircuitManager;
import com.github.ysl3000.quantum.impl.extension.QuantumExtensionLoader;
import com.github.ysl3000.quantum.impl.interfaces.ICircuitActivator;
import com.github.ysl3000.quantum.impl.listeners.QuantumConnectorsBlockListener;
import com.github.ysl3000.quantum.impl.listeners.QuantumConnectorsPlayerListener;
import com.github.ysl3000.quantum.impl.listeners.QuantumConnectorsWorldListener;
import com.github.ysl3000.quantum.impl.nmswrapper.ClassRegistry;
import com.github.ysl3000.quantum.impl.nmswrapper.QSWorld;
import com.github.ysl3000.quantum.impl.receiver.base.Registry;
import com.github.ysl3000.quantum.impl.utils.MessageLogger;
import com.github.ysl3000.quantum.impl.utils.SourceBlockUtil;
import com.github.ysl3000.quantum.impl.utils.VariantWrapper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QuantumConnectors extends JavaPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantumConnectors.class);

    private int maxDelayTime = 10;//in seconds
    private int maxReceiversPerCircuit = 20;
    private boolean verboseLogging = false;
    private int autosaveInterval = 30;//specified here in minutes
    private int autosaveId = -1;
    private String apiVersion;
    // Configurables
    private int maxChainLinks = 3;
    private QuantumConnectorsAPIImplementation api;
    private Map<String, String> messages;
    private ICircuitActivator circuitManager;
    private boolean updateNotifications = false;
    private String updateName;

    private QuantumExtensionLoader loader;
    private CircuitLoader circuitLoader;

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTask(autosaveId);

        if (circuitManager != null) {
            circuitLoader.saveAllWorlds();
        }

        this.loader.disable();
        this.api.unregisterAll();
    }

    @Override
    public void onEnable() {


        //This might be outdated...
        getDataFolder().mkdirs();

        //Load config options, localized messages
        setupConfig();

        MessageLogger messageLogger = new MessageLogger(this.getLogger(), messages);

        Registry<AbstractReceiver> receiverRegistry = new Registry<>();
        Registry<AbstractCircuit> circuitRegistry = new Registry<>();

        CircuitContainer circuitContainer = new CircuitContainer();

        this.circuitLoader = new CircuitLoader(this, messageLogger, circuitRegistry, circuitContainer);

        this.circuitManager = new CircuitManager(this, circuitRegistry, receiverRegistry, circuitContainer);

        SourceBlockUtil sourceBlockUtil = new SourceBlockUtil();
        ClassRegistry classRegistry = new ClassRegistry();
        this.apiVersion = classRegistry.getApiVersion();
        com.github.ysl3000.quantum.impl.nmswrapper.IQSWorld qsWorld = new QSWorld(classRegistry);
        VariantWrapper variantWrapper = new VariantWrapper();
        this.api = new QuantumConnectorsAPIImplementation(receiverRegistry, circuitRegistry, sourceBlockUtil, qsWorld, variantWrapper, maxChainLinks, this.circuitManager);

        QuantumConnectorsAPI.setApi(this.api);

        File extensionDir = new File(getDataFolder().getPath(), "extensions");

        extensionDir.mkdirs();

        this.loader = new QuantumExtensionLoader(api, messageLogger, extensionDir);

        this.loader.load();
        this.loader.enable(circuitLoader::loadWorlds);


        QuantumConnectorsWorldListener worldListener = new QuantumConnectorsWorldListener(this.circuitLoader);
        QuantumConnectorsBlockListener blockListener = new QuantumConnectorsBlockListener(this, circuitManager, messageLogger, sourceBlockUtil);
        QuantumConnectorsPlayerListener playerListener = new QuantumConnectorsPlayerListener(this, circuitManager, messageLogger, api, receiverRegistry);

        getCommand("qc").setExecutor(new QuantumConnectorsCommandExecutor(this, circuitManager, messageLogger));


        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(playerListener, this);
        pm.registerEvents(blockListener, this);
        pm.registerEvents(worldListener, this);

        autosaveInterval *= 60 * 20;//convert to ~minutes

        autosaveId = getServer().getScheduler().scheduleSyncRepeatingTask(
                this,
                circuitLoader::saveAllWorlds,
                autosaveInterval,
                autosaveInterval);

    }

    private void setupConfig() {
        this.reloadConfig();

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        this.saveConfig();
        verboseLogging = config.getBoolean("verbose_logging", verboseLogging);
        maxChainLinks = config.getInt("max_chain_links", maxChainLinks);
        maxDelayTime = config.getInt("max_delay_time", maxDelayTime);
        maxReceiversPerCircuit = config.getInt("max_receivers_per_circuit", maxReceiversPerCircuit);
        autosaveInterval = config.getInt("autosave_interval_minutes", autosaveInterval);
        updateNotifications = config.getBoolean("update_notifications", updateNotifications);
        this.saveConfig();

        File messagesFile = new File(this.getDataFolder(), "messages.yml");

        messages = new HashMap<>();
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            copy(this.getResource("messages.yml"), messagesFile);
        }

        FileConfiguration messagesYml = YamlConfiguration.loadConfiguration(messagesFile);

        Set<String> messageList = messagesYml.getKeys(false);

        for (String m : messageList) {
            messages.put(m, messagesYml.getString(m));
        }
    }

    private void copy(InputStream in, File file) {
        try (OutputStream out = new FileOutputStream(file)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
        } catch (Exception e) {
            LOGGER.error("Copy of resource failed!", e);
        }
    }


    public boolean isUpdateAvailable() {
        return false;
    }

    public String getUpdateName() {
        return updateName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public int getMaxDelayTime() {
        return maxDelayTime;
    }

    public int getMaxReceiversPerCircuit() {
        return maxReceiversPerCircuit;
    }

    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    public int getAutosaveInterval() {
        return autosaveInterval;
    }
}
