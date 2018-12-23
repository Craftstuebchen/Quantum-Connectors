package com.github.ysl3000.quantum.impl.nmswrapper;

import org.bukkit.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


public class QSWorld implements IQSWorld {

    private static final Logger LOGGER = LoggerFactory.getLogger(QSWorld.class);

    private ClassRegistry classRegistry;

    public QSWorld(ClassRegistry classRegistry) {
        this.classRegistry = classRegistry;
    }

    @Override
    public void setStatic(World world, boolean isStatic) {
        try {
            Object nmsWorld = classRegistry.getNmsWorldField().invoke(world);
            Field field = classRegistry.getIsClientSide();
            field.setAccessible(true);
            field.set(nmsWorld, isStatic);

        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("World couldn't be toggled static.", e);
        }
    }
}
