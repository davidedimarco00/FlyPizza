package env.behavior;

import env.FlyPizzaModel;
import jason.environment.grid.Location;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

/**This class manage the drone movements*/

public class NavigationManager {

    private final int GSize;
    private final Set<Location> obstacles;
    private final Set<Location> occupiedLocations;
    private final Random random = new Random();
    private FlyPizzaModel model;

    public NavigationManager(int gridSize, Set<Location> obstacles, Set<Location> occupiedLocations, FlyPizzaModel model) {
        this.GSize = gridSize;
        this.obstacles = obstacles;
        this.occupiedLocations = occupiedLocations;
        this.model = model;
    }

    public void moveTowards(final Location dest, final int agentId) {
        Location agentPosition = model.getAgPos(agentId);
        moveTowardsDestination(dest, agentPosition);
        if (moveIfNotOccupied(agentId, agentPosition) ) {
            model.getView().update(agentPosition.x, agentPosition.y);
            return;
        }

        Location newLocation = moveAroundTheObstacle(dest, agentId);
        moveIfNotOccupied(agentId, newLocation);
    }

    private synchronized boolean moveIfNotOccupied(int agentId, Location agentPosition) {
        if ((!isOccupied(agentPosition, agentId) || isAllowedPosition(agentPosition))) {
            model.setAgPos(agentId, agentPosition);
            if (model.getView() != null) {
                model.getView().update(agentPosition.x, agentPosition.y);
            }
            return true;
        }
        return false;
    }

    private boolean isAllowedPosition(Location loc) {
        return loc.equals(model.getPizzeria().getLocation());
    }

    private Location moveAroundTheObstacle(Location dest, int agentId) {
        Location agentPosition = model.getAgPos(agentId);
        boolean randomDirection = random.nextBoolean();
        int newx = 0;
        int newy = 0;
        if(agentPosition.y < dest.y) {
            newy = agentPosition.y + 1;
            if(agentPosition.x == dest.x) {
                newx = randomDirection ? agentPosition.x + 1 : agentPosition.x - 1;
            }
            if(agentPosition.x < dest.x) {
                newx = agentPosition.x + (randomDirection ? 1 : 0);
            }
            if(agentPosition.x > dest.x) {
                newx = agentPosition.x - (randomDirection ? 1 : 0);
            }
        }

        if(agentPosition.y > dest.y) {
            newy = agentPosition.y - 1;
            if(agentPosition.x == dest.x) {
                newx = randomDirection ? agentPosition.x + 1 : agentPosition.x - 1;
            }
            if(agentPosition.x < dest.x) {
                newx = agentPosition.x + (randomDirection ? 1 : 0);
            }
            if(agentPosition.x > dest.x) {
                newx = agentPosition.x - (randomDirection ? 1 : 0);
            }
        }

        if(agentPosition.y == dest.y) {
            newy = randomDirection ? agentPosition.y + 1 : agentPosition.y - 1;
            if(agentPosition.x < dest.x) {
                newx = agentPosition.x + (randomDirection ? 1 : 0);
            }
            if(agentPosition.x > dest.x) {
                newx = agentPosition.x - (randomDirection ? 1 : 0);
            }
            if(agentPosition.x == dest.x) {
                newx = randomDirection ? agentPosition.x + 1 : agentPosition.x - 1;
            }
        }

        agentPosition.x = validateCoords(agentPosition.x, newx);
        agentPosition.y = validateCoords(agentPosition.y, newy);
        return agentPosition;
    }

    private boolean isOccupied(Location agentPosition, int agentId) {
        for (int i = 0; i < model.getNbOfAgs(); i++) {
            if (i != agentId && model.getAgPos(i).equals(agentPosition) && agentId !=4) {
                return true;
            }
        }
        return obstacles.contains(agentPosition) || occupiedLocations.contains(agentPosition);
    }

    private void moveTowardsDestination(Location dest, Location agentPosition) {
        if (agentPosition.x < dest.x) {
            agentPosition.x++;
        } else if (agentPosition.x > dest.x) {
            agentPosition.x--;
        }
        if (agentPosition.y < dest.y) {
            agentPosition.y++;
        } else if (agentPosition.y > dest.y) {
            agentPosition.y--;
        }
    }

    public int validateCoords(int currentCoord, int newCoord) {
        return newCoord < 0 || newCoord >= GSize ? currentCoord : newCoord;
    }
}
