import static org.junit.jupiter.api.Assertions.*;

import env.model.FlyPizzaModel;
import env.model.behavior.NavigationManager;
import env.view.FlyPizzaView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jason.environment.grid.Location;
import java.util.HashSet;
import java.util.Set;

public class NavigationManagerTest {

    private FlyPizzaModel model;
    private NavigationManager navigationManager;
    private Set<Location> obstacles;
    private Set<Location> occupiedLocations;

    @BeforeEach
    public void setUp() {
        int gridSize = 50;
        obstacles = new HashSet<>();
        occupiedLocations = new HashSet<>();
        model = new FlyPizzaModel(3);
        FlyPizzaView view = new FlyPizzaView(model);
        model.setView(view);
        navigationManager = new NavigationManager(gridSize, obstacles, occupiedLocations, model);
    }

    //verify if the drone moves around the obstacle
    @Test
    public void testMoveAroundObstacle() {
        int agentId = 0;
        Location destination = new Location(10, 10);

        //creo un ostacolo vicino alla destinazione
        Location obstacle = new Location(9, 9);
        obstacles.add(obstacle);

        navigationManager.moveTowards(destination, agentId);
        Location newPosition = model.getAgPos(agentId);

        assertNotEquals(obstacle, newPosition);
    }

    //Verify that the drone doesn't move in a occupied location
    @Test
    public void testMoveIfOccupied() {
        int agentId = 0;
        Location initialPosition = model.getAgPos(agentId);
        Location occupiedLocation = new Location(initialPosition.x + 1, initialPosition.y + 1);
        occupiedLocations.add(occupiedLocation);
        //move the drone
        navigationManager.moveTowards(occupiedLocation, agentId);
        Location newPosition = model.getAgPos(agentId);
        assertNotEquals(occupiedLocation, newPosition);
    }

    //verify coordinates
    @Test
    public void testValidateCoords() {
        int validCoord = 5;
        int negativeCoord = -1;
        int tooLargeCoord = 51;

        int resultValid = navigationManager.validateCoords(validCoord, 6);
        int resultNegative = navigationManager.validateCoords(validCoord, negativeCoord);
        int resultTooLarge = navigationManager.validateCoords(validCoord, tooLargeCoord);

        assertEquals(6, resultValid, "Valid coordinate should be updated.");
        assertEquals(validCoord, resultNegative, "Negative coordinate should not be allowed.");
        assertEquals(validCoord, resultTooLarge, "Coordinate exceeding grid size should not be allowed.");
    }

}
