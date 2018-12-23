package com.github.ysl3000.quantum.impl;

import be.seeseemelk.mockbukkit.WorldMock;
import com.github.ysl3000.quantum.api.circuit.AbstractCircuit;
import org.bukkit.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

/**
 * Created by ysl3000
 */
public class CircuitContainerTest {

    private CircuitContainer circuitContainer;

    private UUID playerUUID = UUID.randomUUID();


    private AbstractCircuit circuit;

    private WorldMock world = new WorldMock();


    @Before
    public void setup() {

        world.setName("world");
        Location location = new Location(world, 0, 0, 0);

        circuitContainer = new CircuitContainer();
        circuit = Mockito.mock(AbstractCircuit.class);

        Mockito.when(circuit.getLocation()).thenReturn(location);

    }

    @Test
    public void shouldHaveAPendingCircuit() {

        circuitContainer.addPendingCircuit(playerUUID, circuit);
        Assert.assertTrue("Player should have one pending circuit", circuitContainer.hasPendingCircuit(playerUUID));
        circuitContainer.removePendingCircuit(playerUUID);
        Assert.assertFalse("Player should have no pending circuit anymore", circuitContainer.hasPendingCircuit(playerUUID));
    }
    @Test
    public void shouldNotHaveAPendingCircuit() {
        Assert.assertFalse("Player should have no pending circuit", circuitContainer.hasPendingCircuit(playerUUID));
    }

    @Test
    public void worldShouldContainOneCircuit(){
        circuitContainer.addCircuit(circuit);
        Assert.assertEquals("World should contain one circuit", 1,circuitContainer.getCircuitCount("world"));
    }
}
