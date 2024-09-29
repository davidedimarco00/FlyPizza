package env;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.Location;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlyPizzaEnv extends Environment {

    static Logger logger = Logger.getLogger(FlyPizzaEnv.class.getName());
    final FlyPizzaModel model = new FlyPizzaModel();
    final FlyPizzaView view = new FlyPizzaView(model);

    private Location lDrone;


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
        // Start DroneHandlers for each drone
        executorService = Executors.newFixedThreadPool(3); // Assuming 3 drones
        for (int i = 0; i < 3; i++) {
            String droneName = "drone" + (i + 1);
            DroneHandler handler = new DroneHandler(droneName, i, this);
            executorService.submit(handler);
        }
    }

    public synchronized void updateDronePercepts(String droneName, int droneId) {
        this.clearPercepts(droneName);
        // Update drone perceptions
        lDrone = model.getAgPos(droneId);
        int batteryLevel = model.getBatteryLevel(droneName);
        this.addPercept(droneName, Literal.parseLiteral("current_position(" + lDrone.x + "," + lDrone.y + ")"));
        this.addPercept(droneName, Literal.parseLiteral("batteryLevel(" + batteryLevel + ")"));
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
                model.decreaseBatteryLevel(ag); //e per ogni passo si scarica la batteria
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
            logger.log(Level.INFO, "CHIAMATA A charge drone: " + xTerm.toString());
            model.setBatteryLevel(xTerm.toString(), 100);
        }

        // Update perceptions for this drone
        updateDronePercepts(ag, agId);

        return result;
    }

    private int getAgIdBasedOnName(String agName) {
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

        public DroneHandler(String droneName, int droneId, FlyPizzaEnv environment) {
            this.droneName = droneName;
            this.droneId = droneId;
            this.environment = environment;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // Update perceptions for the drone
                    environment.updateDronePercepts(droneName, droneId);
                    Thread.sleep(200L);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    
}
