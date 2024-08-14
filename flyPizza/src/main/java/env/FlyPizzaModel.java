package env;

import env.objects.ObjectsID;
import env.objects.chargingBase.ChargingBase;
import env.objects.chargingBase.ChargingBaseStatus;
import env.objects.garage.Garage;
import env.objects.pizzeria.Pizzeria;
import env.objects.robot.RescueRobot;
import env.objects.robot.RobotStatus;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.*;

public class FlyPizzaModel extends GridWorldModel {

    // Grid dimension
    public static final int GSize = 50;

    // Objects in grid
    Set<Location> obstacles = new HashSet<>();
    ChargingBase chargingBase1 = new ChargingBase(new Location(30, 32), ChargingBaseStatus.FREE, ObjectsID.CHARGING_BASE1.getId());
    ChargingBase chargingBase2 = new ChargingBase(new Location(48, 10), ChargingBaseStatus.FREE, ObjectsID.CHARGING_BASE2.getId());
    ChargingBase chargingBase3 = new ChargingBase(new Location(1, 1), ChargingBaseStatus.FREE, ObjectsID.CHARGING_BASE3.getId());
    Garage garage = new Garage(new Location(25, 26), ObjectsID.GARAGE.getId()); // da cambiare nome in box
    Pizzeria pizzeria = new Pizzeria(new Location(26, 26), ObjectsID.PIZZERIA.getId());
    RescueRobot robot = new RescueRobot(new Location(27, 26), 100, RobotStatus.AVAILABLE, ObjectsID.ROBOT.getId());

    // Model variables
    public int numberOfAvailablePizzas = pizzeria.getNumberOfPizzas();
    public int numberOfDroneInGarage = garage.getNumberOfParkedDrones();
    public int robotBatteryLevel = robot.getBatteryLevel();

    // Random utils
    Random random = new Random();

    public FlyPizzaModel() {
        super(FlyPizzaModel.GSize, FlyPizzaModel.GSize, 7);
        this.addObjects();
    }

    public void drawObstacle(int x, int y) {
        Location obstacleLocation = new Location(x, y);
        obstacles.add(obstacleLocation);
        if (view != null) {
            view.update(x, y);
        }
    }

    boolean moveTowards(final Location dest, final int agentId) {
        Location r1 = this.getAgPos(agentId);

        // Compute where to move
        moveTowardsDestination(dest, r1);

        // Try to reach the new position
        if (moveIfNotOccupied(agentId, r1)) return true;

            // Avoid the obstacle
        else {
            r1 = moveAroundTheObstacle(dest, agentId);
            if (moveIfNotOccupied(agentId, r1)) return true;
        }

        return true;
    }

    private synchronized boolean moveIfNotOccupied(int agentId, Location r1) {
        if (!isOccupied(r1, agentId)) {
            // Updates the agent's coordinates by moving it
            this.setAgPos(agentId, r1);

            // Repaint rack and delivery to update colors
            if (this.view != null) {
                updateView();
            }
            return true;
        }
        return false;
    }

    private Location moveAroundTheObstacle(Location dest, int agentId) {
        Location r1;
        r1 = this.getAgPos(agentId);

        boolean randomDirection = random.nextBoolean();
        int newx = 0;
        int newy = 0;

        // The destination is below
        if (r1.y < dest.y) {
            // Can only move downwards
            newy = r1.y + 1;

            // Same x: can try to move left or right
            if (r1.x == dest.x) {
                newx = randomDirection ? r1.x + 1 : r1.x - 1;
            }
            if (r1.x < dest.x) {
                newx = r1.x + (randomDirection ? 1 : 0);
            }
            if (r1.x > dest.x) {
                newx = r1.x - (randomDirection ? 1 : 0);
            }
        }

        // The destination is above
        if (r1.y > dest.y) {
            // Can only move upwards
            newy = r1.y - 1;

            // Same x: can try moving left or right
            if (r1.x == dest.x) {
                newx = randomDirection ? r1.x + 1 : r1.x - 1;
            }
            if (r1.x < dest.x) {
                newx = r1.x + (randomDirection ? 1 : 0);
            }
            if (r1.x > dest.x) {
                newx = r1.x - (randomDirection ? 1 : 0);
            }
        }

        // The destination is at the same height
        if (r1.y == dest.y) {
            // Can go up or down
            newy = randomDirection ? r1.y + 1 : r1.y - 1;

            // Dest to my right
            if (r1.x < dest.x) {
                newx = r1.x + (randomDirection ? 1 : 0);
            }

            // Dest to my left
            if (r1.x > dest.x) {
                newx = r1.x - (randomDirection ? 1 : 0);
            }

            // Dest with my same x
            if (r1.x == dest.x) {
                newx = randomDirection ? r1.x + 1 : r1.x - 1;
            }
        }

        r1.x = validateCoords(r1.x, newx);
        r1.y = validateCoords(r1.y, newy);
        return r1;
    }

    private void moveTowardsDestination(Location dest, Location r1) {
        if (r1.x < dest.x) {
            r1.x++;
        } else if (r1.x > dest.x) {
            r1.x--;
        }
        if (r1.y < dest.y) {
            r1.y++;
        } else if (r1.y > dest.y) {
            r1.y--;
        }
    }

    public int validateCoords(int currentCoord, int newCoord) {
        return newCoord < 0 || newCoord > this.GSize - 1 ? currentCoord : newCoord;
    }

    private boolean isOccupied(Location loc, int agentId) {
        for (int i = 0; i < this.getNbOfAgs(); i++) {
            if (i != agentId && this.getAgPos(i).equals(loc)) {
                return true; // The position is occupied by another agent
            }
        }
        return false; // The position is free
    }

    boolean isCollision(Location loc) {
        return obstacles.contains(loc) || isOccupiedByDrone(loc);
    }

    private boolean isOccupiedByDrone(Location loc) {
        for (int i = 0; i < getNbOfAgs(); i++) {
            Location droneLoc = getAgPos(i);
            if (droneLoc.equals(loc)) {
                return true;
            }
        }
        return false;
    }


    private boolean allDronesAtTarget(Location target) {
        for (int i = 0; i < 5; i++) {
            if (!getAgPos(i).equals(target)) {

                return false;

            }
        }
        return true;
    }



    //GETTER

    public ChargingBase getChargingBase1() {
        return chargingBase1;
    }

    public ChargingBase getChargingBase2() {
        return chargingBase2;
    }

    public ChargingBase getChargingBase3() {
        return chargingBase3;
    }

    public Garage getGarage() {
        return garage;
    }

    public Pizzeria getPizzeria() {
        return pizzeria;
    }

    public RescueRobot getRobot() {
        return robot;
    }

    //METODI GRAFICI
    private void updateView() {
        if (this.view != null) {
            this.view.update(this.chargingBase1.getLocation().x, this.chargingBase1.getLocation().y);
            this.view.update(this.chargingBase2.getLocation().x, this.chargingBase2.getLocation().y);
            this.view.update(this.chargingBase3.getLocation().x, this.chargingBase3.getLocation().y);
            this.view.update(this.garage.getLocation().x, this.garage.getLocation().y);
            this.view.update(this.pizzeria.getLocation().x, this.pizzeria.getLocation().y);
            this.view.update(this.robot.getLocation().x, this.robot.getLocation().y);
        }
    }


    public Set<Location> getObstacles() {
        return obstacles;
    }


    private void addObjects(){
        int numberOfObstacles = 50;

        // Creazione di un set di posizioni già occupate
        Set<Location> occupiedLocations = new HashSet<>();
        occupiedLocations.add(chargingBase1.getLocation());
        occupiedLocations.add(chargingBase2.getLocation());
        occupiedLocations.add(chargingBase3.getLocation());
        occupiedLocations.add(garage.getLocation());
        occupiedLocations.add(pizzeria.getLocation());
        occupiedLocations.add(robot.getLocation());

        // Posizionamento iniziale dei droni (tutti partono dal garage)
        for (int i = 0; i < 5; i++) {
            this.setAgPos(i, garage.getLocation());
            occupiedLocations.add(garage.getLocation()); // aggiunta della posizione del drone al set
        }

        // Generazione di ostacoli casuali
        for (int i = 0; i < numberOfObstacles; i++) {
            Location location;
            do {
                location = new Location(random.nextInt(GSize), random.nextInt(GSize));
            } while (occupiedLocations.contains(location)); // ripete finché la posizione è occupata

            obstacles.add(location);
            occupiedLocations.add(location); // segna la posizione come occupata
        }

        // Posizione iniziale della pizzeria
        this.setAgPos(5, pizzeria.getLocation());

        // Posizione iniziale del robot di rescue
        this.setAgPos(6, robot.getLocation());

        // Aggiunta degli oggetti all'ambiente
        this.add(ObjectsID.CHARGING_BASE1.getValue(), chargingBase1.getLocation());
        this.add(ObjectsID.CHARGING_BASE2.getValue(), chargingBase2.getLocation());
        this.add(ObjectsID.CHARGING_BASE3.getValue(), chargingBase3.getLocation());
        this.add(ObjectsID.PIZZERIA.getValue(), pizzeria.getLocation());
        this.add(ObjectsID.GARAGE.getValue(), garage.getLocation()); // Correzione del posizionamento del garage
        this.add(ObjectsID.ROBOT.getValue(), robot.getLocation());

        for (Location loc : obstacles) {
            this.drawObstacle(loc.x, loc.y);
        }
    }



}
