package com.github.ysl3000.quantum.impl.extension;

import com.github.ysl3000.quantum.api.IQuantumConnectorsAPI;
import com.github.ysl3000.quantum.api.QuantumExtension;
import com.github.ysl3000.quantum.impl.utils.MessageLogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipFile;

/**
 * Created by Yannick on 23.01.2017.
 */
public class QuantumExtensionLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuantumExtensionLoader.class);

    private final IQuantumConnectorsAPI api;
    private final MessageLogger messageLogger;
    private final Set<QuantumExtension> quantumExtensions = new HashSet<>();
    private final Set<Class<?>> quantumExtensionsClass = new HashSet<>();
    private final File file;

    public QuantumExtensionLoader(IQuantumConnectorsAPI api, MessageLogger messageLogger, File file) {
        this.api = api;
        this.messageLogger = messageLogger;
        this.file = file;
    }


    public void load() {

        if (file.exists() && file.isDirectory()) {

            File[] files = file.listFiles((dir, name) -> name.endsWith(".jar"));

            for (File jar : Objects.requireNonNull(files)) {
                String mainClass;
                try (ZipFile zipFile = new ZipFile(jar)) {
                    InputStream is = zipFile.getInputStream(zipFile.getEntry("extension.yml"));

                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(is));
                    mainClass = config.getString("main");
                    loadClass(jar, mainClass);

                } catch (IOException e) {
                    LOGGER.error("Error while loading module file {} error: {}", jar.getName(), e);
                }


            }


        }
    }

    private void loadClass(File jar, String mainClass) {
        try (URLClassLoader l = URLClassLoader.newInstance(new URL[]{jar.toURI().toURL()}, getClass().getClassLoader())) {
            Class<?> clazz = l.loadClass(mainClass);
            quantumExtensionsClass.add(clazz);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Class not found! Wrong main defined in extension.yml?: {} class: {} error: {}", jar.getName(), mainClass, e);
        } catch (MalformedURLException e) {
            LOGGER.error("Fully qualified classname invalid classname: {} error: {}", mainClass, e);
        } catch (IOException e) {
            LOGGER.error("Jar not readable or not a jar: {} error: {}", jar.getName(), e);
        }
    }


    public void enable(CallBack onFinish) {
        for (Class<?> clazz : quantumExtensionsClass) {

            try {

                if (QuantumExtension.class.isAssignableFrom(clazz)) {
                    Constructor<? extends QuantumExtension> quantumExtensionConstructor = clazz.asSubclass(QuantumExtension.class).getConstructor();

                    QuantumExtension quantumExtension = quantumExtensionConstructor.newInstance();
                    quantumExtension.onEnable(api);
                    quantumExtensions.add(quantumExtension);
                    messageLogger.log(Level.INFO, quantumExtension.getExtensionName() + " enabled!");

                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.error("Instantiation of QuantumExtension failed!", e);
            }
        }

        onFinish.call();
    }

    public void disable() {
        for (QuantumExtension extension : quantumExtensions) {
            extension.onDisable();
            LOGGER.info("{} disabled", extension.getExtensionName());
        }
    }

    @FunctionalInterface
    public interface CallBack {
        void call();
    }


}
