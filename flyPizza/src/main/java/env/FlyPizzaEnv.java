package env;

import env.model.FlyPizzaModel;
import env.view.FlyPizzaView;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.Location;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlyPizzaEnv extends Environment {

    static Logger logger = Logger.getLogger(FlyPizzaEnv.class.getName());
    final FlyPizzaModel model = new FlyPizzaModel();
    final FlyPizzaView view = new FlyPizzaView(model);

    private final int DRONE_NUMBER = 3;
    private Location lDrone;
    private Location lRobot;
    ExecutorService executorService;


    /* Action Literal */
    public static final Literal moveAction = Literal.parseLiteral("move");
    public static final Literal pizzaDelivery = Literal.parseLiteral("deliverPizza");

    @Override
    public void init(final String[] args) {
        logger.log(Level.INFO, "OK, environment ready");
        if ((args.length == 1) && args[0].equals("gui")) {
            this.model.setView(view);
        }
        //Create a thread for each drone
        executorService = Executors.newFixedThreadPool(DRONE_NUMBER); // Assuming 3 drones
        for (int i = 0; i < this.DRONE_NUMBER; i++) {
            String droneName = "drone" + (i + 1);
            DroneHandler handler = new DroneHandler(droneName, i, this);
            executorService.submit(handler);
        }
        //Add perceptions to pizzeria
        this.updatePizzeriaPercepts();
    }



    public synchronized void updatePercepts(String droneName, int droneId) {
        this.clearPercepts(droneName);
        // Update drone perceptions
        lDrone = model.getAgPos(droneId);
        if (droneName.contains("drone")) {
            int batteryLevel = model.getBatteryLevel(droneName);
            String isBroken = model.isDroneBroken(droneName);
            this.addPercept(droneName, Literal.parseLiteral("batteryLevel(" + batteryLevel + ")"));
            this.addPercept(droneName, Literal.parseLiteral("broken(" + droneName + "," + isBroken + ")"));
            this.addPercept(droneName, Literal.parseLiteral("current_position(" + lDrone.x + "," + lDrone.y + ")"));

        }
        this.updateRobotPercepts("robot");


    }

    public synchronized void updatePizzeriaPercepts() {
        String pizzeriaAgentName = "pizzeria";
        this.clearPercepts(pizzeriaAgentName);
        this.addPercept(pizzeriaAgentName, Literal.parseLiteral("maxPizzas(" + this.model.getPizzeria().getMaxPizzas() + ")"));
    }

    public synchronized void updateRobotPercepts(String robotName) {
        this.clearPercepts(robotName);
        lRobot = model.getAgPos(4);
        this.addPercept(robotName, Literal.parseLiteral("current_position(" + lRobot.x + "," + lRobot.y + ")"));
    }



    @Override
    public synchronized boolean executeAction(final String ag, final Structure action) {
        boolean result = true;
        int agId = getAgIdBasedOnName(ag);

        if (action.getFunctor().equals("move")) {
            // Extract destination coordinates
            Term xTerm = action.getTerm(0);
            Term yTerm = action.getTerm(1);
            try {
                int x = Integer.parseInt(xTerm.toString());
                int y = Integer.parseInt(yTerm.toString());
                model.moveTowards(new Location(x, y), agId); //vado avanti
                if (ag.contains("drone")) { //se è il drone a muoversi
                    model.decreaseBatteryLevel(ag); //e per ogni passo si scarica la batteria
                } //altrimenti vuol dire che il metodo lo sta usando il robot
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Error parsing coordinates", e);
                result = false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (action.getFunctor().equals("pizza_delivered")) {
            // Drone has delivered a pizza
            model.getPizzeria().removePizzas(1);
        }
        else if (action.getFunctor().equals("charge_drone")) {
            Term xTerm = action.getTerm(0);
            model.setBatteryLevel(xTerm.toString(), 100);
        }  else if (action.getFunctor().equals("repair_drone")) {
            Term xTerm = action.getTerm(0);
            model.setBatteryLevel(xTerm.toString(), 100);
            model.repairDrone(xTerm.toString());
            model.setAgPos(getAgIdBasedOnName(xTerm.toString()), 26 ,26);



        } else if (action.getFunctor().equals("drop_off_drone")) {
            Term xTerm = action.getTerm(0);
            //model.dropOffDrone(xTerm.toString());
        }

        // Update perceptions for this drone
        updatePercepts(ag, agId);

        return result;
    }

    public int getAgIdBasedOnName(String agName) {
        switch (agName) {
            case "drone1":
                return 0;
            case "drone2":
                return 1;
            case "drone3":
                return 2;
            case "pizzeria":
                return 3;
            case "robot":
                return 4;
            default:
                return -1;
        }
    }



    @Override
    public void stop() {
        super.stop();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }



    //MULTI THREAD, for each drone exists a thread Runnable
    private class DroneHandler implements Runnable {
        private String droneName;
        private int droneId;
        private FlyPizzaEnv environment;
        private Random random;
        private int failureCheckInterval;

        public DroneHandler(String droneName, int droneId, FlyPizzaEnv environment) {
            this.droneName = droneName;
            this.droneId = droneId;
            this.environment = environment;
            this.random = new Random();
            this.failureCheckInterval = 10000 + random.nextInt(5000);
        }

        @Override
        public void run() {
            try {
                long lastFailureCheck = System.currentTimeMillis();
                while (true) {
                    environment.updatePercepts(droneName, droneId);
                    // Controlla se simulare un guasto
                    if (System.currentTimeMillis() - lastFailureCheck >= failureCheckInterval) {
                        lastFailureCheck = System.currentTimeMillis();
                        simulateRandomFailure();
                    }
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        private void simulateRandomFailure() {
            //genera un numero casuale tra 0 e 1
            double randomNumber = random.nextDouble();
            if (randomNumber < 0.25) { //indica la probabilità di rottura
                if (Objects.equals(model.isDroneBroken(droneName), "no")) { //se non sono rotto allora mi rompo
                    model.setDroneBroken(droneName, "yes");
                    logger.log(Level.INFO, droneName + " si è guastato");
                }
            }
        }
    }

}
