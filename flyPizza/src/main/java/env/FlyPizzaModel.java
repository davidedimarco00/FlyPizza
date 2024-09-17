package env;

import env.behavior.NavigationManager;
import env.objects.ObjectsID;
import env.objects.chargingBase.ChargingBase;
import env.objects.chargingBase.ChargingBaseStatus;
import env.objects.pizzeria.Pizzeria;
import env.objects.robot.RescueRobot;
import env.objects.robot.RobotStatus;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.*;

public class FlyPizzaModel extends GridWorldModel {

    public static final int GSize = 50;

    private Set<Location> obstacles = new HashSet<>();
    private List<ChargingBase> chargingBases = new ArrayList<>();

    private Pizzeria pizzeria = new Pizzeria(new Location(26, 26), ObjectsID.PIZZERIA.getId());
    private RescueRobot robot = new RescueRobot(new Location(27, 26), 100, RobotStatus.AVAILABLE, ObjectsID.ROBOT.getId());

    private Random random = new Random();
    private NavigationManager navigationManager;

    static Set<Location> occupiedLocations = new HashSet<>();

    //private List<Drone> drones = new ArrayList<>();

    public FlyPizzaModel() {
        super(FlyPizzaModel.GSize, FlyPizzaModel.GSize, 5);
        this.navigationManager = new NavigationManager(GSize, obstacles, occupiedLocations, this);
        this.addObjects();
        //this.initializeDrones(); //is this useful, insert each drone in a separate thread ??
    }



   /* private void initializeDrones() {
        for (int i = 0; i < 3; i++) {
            Drone drone = new Drone(i);
            drones.add(drone);
            drone.start();
        }
    }*/

    private void addObjects() {
        int numberOfObstacles = 50;
        //Add recharge base to the map
        chargingBases.add(new ChargingBase(new Location(30, 32), ChargingBaseStatus.FREE,
                                                                    ObjectsID.CHARGING_BASE1.getValue(),
                                                                    ObjectsID.CHARGING_BASE1.getObjectStringName()));
        chargingBases.add(new ChargingBase(new Location(48, 10), ChargingBaseStatus.FREE,
                                                                    ObjectsID.CHARGING_BASE2.getValue(),
                                                                    ObjectsID.CHARGING_BASE2.getObjectStringName()));
        chargingBases.add(new ChargingBase(new Location(1, 1), ChargingBaseStatus.FREE,
                                                        ObjectsID.CHARGING_BASE3.getValue(),
                                                        ObjectsID.CHARGING_BASE3.getObjectStringName()));

        //Set the position of the drones equals to pizzeria location
        for (int i = 0; i < 3; i++) {
            this.setAgPos(i, pizzeria.getLocation().x, pizzeria.getLocation().y);
        }

        //Add obstacles
        for (int i = 0; i < numberOfObstacles; i++) {
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
        // Aggiungi le basi di ricarica alla mappa
        for (ChargingBase base : chargingBases) {
            this.add(base.getId(), base.getLocation());
        }
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

   /* private class Drone extends Thread {
        private int id;
        private Location destination;

        public Drone(int id) {
            this.id = id;
        }

        public void setDestination(Location destination) {
            this.destination = destination;
        }

        @Override
        public void run() {

        }
    }*/

    // GETTER E SETTER

    public static Set<Location> getOccupiedLocations() {
        return occupiedLocations;
    }

    public Set<Location> getObstacles() {
        return obstacles;
    }

    public List<ChargingBase> getChargingBases() {
        return chargingBases;
    }

    public Pizzeria getPizzeria() {
        return pizzeria;
    }

    public RescueRobot getRobot() {
        return robot;
    }

    public FlyPizzaView getView() {
        return (FlyPizzaView) this.view;
    }
}
