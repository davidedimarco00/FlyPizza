import static org.junit.jupiter.api.Assertions.*;

import env.model.FlyPizzaModel;
import env.model.objects.EngineMode;
import env.view.FlyPizzaView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jason.environment.grid.Location;

public class ModelTest {

    private FlyPizzaModel model;
    private FlyPizzaView view;

    @BeforeEach
    public void setUp() {
        model = new FlyPizzaModel(3);
        this.view = new FlyPizzaView(model);
    }

    //test init
    @Test
    public void testInitializeDrones() {
        Location pizzeriaLocation = model.getPizzeria().getLocation();
        for (int i = 0; i < 3; i++) {
            assertEquals(pizzeriaLocation, model.getAgPos(i), "Drone " + i + " should be at pizzeria.");
        }
    }

    //test decreaseBattery
    @Test
    public void testDecreaseBatteryLevel() {
        String droneName = "drone1";
        int initialBattery = model.getBatteryLevel(droneName);

        //FULL MODE
        model.decreaseBatteryLevel(droneName, "full");
        assertEquals(initialBattery - 2, model.getBatteryLevel(droneName));

        //LOW MODE
        model.decreaseBatteryLevel(droneName, "low");
        assertEquals(initialBattery - 3, model.getBatteryLevel(droneName));
    }


    @Test
    public void testRepairDrone() {
        String droneName = "drone1";
        model.setDroneBroken(droneName, "yes");
        assertEquals("yes", model.isDroneBroken(droneName));
        model.repairDrone(droneName); //repair drone
        assertEquals("no", model.isDroneBroken(droneName));
    }

    //move the drone
    @Test
    public void testMoveDrone() {
        String droneName = "drone1";
        Location initialLocation = model.getPizzeria().getLocation();
        Location destination = new Location(10, 10);
        //is the drone at pizzeria ?
        assertEquals(initialLocation, model.getAgPos(0) );
        //move the drone to the pizzeria
        model.moveTowards(destination, 0);
        assertNotEquals(initialLocation, model.getAgPos(0));
    }

    //obstacle creation
    @Test
    public void testObstaclesAdded() {
        assertEquals(80, model.getObstacles().size());
        for (Location obstacle : model.getObstacles()) {
            assertFalse(model.getOccupiedLocations().contains(model.getPizzeria().getLocation()),
                    "Obstacle should not be near the pizzeria.");
        }
    }

    //test engine mode setting
    @Test
    public void testSetEngineMode() {
        String droneName = "drone1";
        model.setEngineMode(droneName, EngineMode.FULL);
        assertEquals(EngineMode.FULL, model.getEngineMode(droneName));
        model.setEngineMode(droneName, EngineMode.LOW);
        assertEquals(EngineMode.LOW, model.getEngineMode(droneName));
    }
}
