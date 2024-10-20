package env.model;

import env.model.objects.*;
import env.view.FlyPizzaView;
import env.model.behavior.NavigationManager;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.*;

public class FlyPizzaModel extends GridWorldModel {

    public static final int GSize = 50;
    private final Set<Location> obstacles = new HashSet<>();
    private final List<Drone> drones = new ArrayList<>();



    private final Pizzeria pizzeria = new Pizzeria(new Location(26, 26), ObjectsID.PIZZERIA.getId());
    private final Robot robot = new Robot(new Location(27, 26), ObjectsID.ROBOT.getId());
    private final Random random = new Random();
    private final NavigationManager navigationManager;
    static Set<Location> occupiedLocations = new HashSet<>();

    public FlyPizzaModel(int nDrone) {
        super(FlyPizzaModel.GSize, FlyPizzaModel.GSize, 5);
        this.navigationManager = new NavigationManager(GSize, obstacles, occupiedLocations, this);
        this.addObjects(nDrone);
    }

    private void addObjects(int nDrone) {
        for (int i = 0; i < nDrone; i++) {
            this.setAgPos(i, pizzeria.getLocation().x, pizzeria.getLocation().y);
            //creo il drone
            Drone drone = new Drone(pizzeria.getLocation(), i, "drone"+(i+1),"no");
            //lo aggiungo alla lista di droni
            this.drones.add(drone);
        }

        //aggiungo gli ostacoli
        int OBSTACLES_NUMBERS = 80;
        for (int i = 0; i < OBSTACLES_NUMBERS; i++) {
            Location location;
            do {
                location = new Location(random.nextInt(GSize), random.nextInt(GSize));
            } while (occupiedLocations.contains(location) || isNearPizzeria(location));
            obstacles.add(location);
            occupiedLocations.add(location);
        }

        //set the pizzeria agent and robot agent location
        this.setAgPos(pizzeria.getId(), pizzeria.getLocation());
        this.setAgPos(robot.getId(), robot.getLocation());

        //add other objects
        this.add(ObjectsID.PIZZERIA.getValue(), pizzeria.getLocation());
        this.add(ObjectsID.ROBOT.getValue(), robot.getLocation());;
    }

    //check if the position is near pizzeria.

    private boolean isNearPizzeria(Location loc) {
        Location pizzeriaLoc = pizzeria.getLocation();
        int dx = Math.abs(loc.x - pizzeriaLoc.x);
        int dy = Math.abs(loc.y - pizzeriaLoc.y);
        return dx <= 2 && dy <= 2;
    }

    public void moveTowards(final Location dest, final int agentId) {
        this.navigationManager.moveTowards(dest, agentId);
    }

    public void decreaseBatteryLevel(final String droneName, String mode) {
        Drone drone = this.findDroneByName(droneName);
        int drainRate = 10;
        if (mode.equals("full")) {
            drainRate = 2;
        } else if (mode.equals("low")) {
            drainRate = 1;
        }
        drone.setBatteryLevel(drone.getBatteryLevel() - drainRate);
    }




    // GETTER E SETTER

    public static Set<Location> getOccupiedLocations() {
        return occupiedLocations;
    }

    public Set<Location> getObstacles() {
        return obstacles;
    }

    public Pizzeria getPizzeria() {
        return pizzeria;
    }

    public Robot getRobot() {
        return robot;
    }

    public FlyPizzaView getView() {
        return (FlyPizzaView) this.view;
    }

    public int getBatteryLevel(String droneName) {
        return this.findDroneByName(droneName).getBatteryLevel();
    }

    public void setBatteryLevel(String droneName, int batteryLevel) {
        this.findDroneByName(droneName).setBatteryLevel(batteryLevel);
    }

    public EngineMode getEngineMode(String droneName) {
        return this.findDroneByName(droneName).getEngineMode();
    }

    public void setEngineMode(String droneName, EngineMode engineMode) {
        this.findDroneByName(droneName).setEngineMode(engineMode);
    }

    public String isDroneBroken(String droneName) {
        return findDroneByName(droneName).getBroken();
    }

    public void setDroneBroken(String droneName, String isBroken) {
        this.findDroneByName(droneName).setBroken(isBroken);
    }

    public void repairDrone(String droneName) {
        this.findDroneByName(droneName).setBroken("no");
    }

    private Drone findDroneByName(String droneName) throws NullPointerException {
        for (Drone d : this.drones) {
            if (d.getDroneName().equals(droneName)) {
                return d;
            }
        }
        throw new NullPointerException();
    }
}
