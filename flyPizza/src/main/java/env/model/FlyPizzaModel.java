package env.model;

import env.view.FlyPizzaView;
import env.model.behavior.NavigationManager;
import env.model.objects.ObjectsID;
import env.model.objects.Pizzeria;
import env.model.objects.Robot;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;
import java.util.concurrent.*;

import java.util.*;

public class FlyPizzaModel extends GridWorldModel {

    public static final int GSize = 50;
    private final int OBSTACLES_NUMBERS = 80;
    private final Set<Location> obstacles = new HashSet<>();
    private final Pizzeria pizzeria = new Pizzeria(new Location(26, 26), ObjectsID.PIZZERIA.getId());
    private final Robot robot = new Robot(new Location(27, 26), 100, ObjectsID.ROBOT.getId());


    private Map<String, Integer> batteryLevels = new ConcurrentHashMap<>(); //mappa drone(x) -> batteryLevel
    private Map<String, String> droneBrokenStatus = new HashMap<>(); //,mappa che associa ogni drone allo stato broken



    private final Random random = new Random();
    private final NavigationManager navigationManager;

    static Set<Location> occupiedLocations = new HashSet<>();

    //private List<Drone> drones = new ArrayList<>();

    public FlyPizzaModel() {
        super(FlyPizzaModel.GSize, FlyPizzaModel.GSize, 5);
        this.navigationManager = new NavigationManager(GSize, obstacles, occupiedLocations, this);
        this.addObjects();
    }

    private void addObjects() {

        //Set the position of the drones equals to pizzeria location and set the map of the drones battery
        for (int i = 0; i < 3; i++) {
            this.setAgPos(i, pizzeria.getLocation().x, pizzeria.getLocation().y);
            this.batteryLevels.put("drone" + String.valueOf(i+1), 100);
            this.droneBrokenStatus.put("drone" + String.valueOf(i+1), "no");
        }

        //Add obstacles
        for (int i = 0; i < this.OBSTACLES_NUMBERS; i++) {
            Location location;
            do {
                location = new Location(random.nextInt(GSize), random.nextInt(GSize));
            } while (occupiedLocations.contains(location) || isNearPizzeria(location));
            obstacles.add(location);
            occupiedLocations.add(location);
        }

        //set the pizzeria agent and robot agent location
        this.setAgPos(3, pizzeria.getLocation());
        this.setAgPos(4, robot.getLocation());

        //Add other object in the map
        this.add(ObjectsID.PIZZERIA.getValue(), pizzeria.getLocation());
        this.add(ObjectsID.ROBOT.getValue(), robot.getLocation());
    }

    private boolean isNearPizzeria(Location loc) {
        Location pizzeriaLoc = pizzeria.getLocation();
        int dx = Math.abs(loc.x - pizzeriaLoc.x);
        int dy = Math.abs(loc.y - pizzeriaLoc.y);
        return dx <= 2 && dy <= 2;
    }

    public void moveTowards(final Location dest, final int agentId) {
        this.navigationManager.moveTowards(dest, agentId);
    }

    public void decreaseBatteryLevel(final String droneName) {
        try {
            int batteryLevel = batteryLevels.get(droneName);
            batteryLevels.put(droneName, batteryLevel - 1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
        return this.batteryLevels.get(droneName);
    }

    public void setBatteryLevel(String droneName, int batteryLevel) {
        batteryLevels.put(droneName, batteryLevel);
    }

    public String isDroneBroken(String droneName) {
        return droneBrokenStatus.get(droneName);
    }

    public void setDroneBroken(String droneName, String isBroken) {
        droneBrokenStatus.put(droneName, isBroken);
    }

    public void repairDrone(String droneName) {
        droneBrokenStatus.put(droneName, "no");
    }
}
