package env;

import env.model.FlyPizzaModel;
import env.model.objects.ObjectsID;
import env.view.FlyPizzaView;
import env.model.behavior.DroneHandler;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.Location;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlyPizzaEnv extends Environment {

    static Logger logger = Logger.getLogger(FlyPizzaEnv.class.getName());
    final FlyPizzaModel model = new FlyPizzaModel(3);
    final FlyPizzaView view = new FlyPizzaView(model);
    ExecutorService executorService;

    @Override
    public void init(final String[] args) {
        logger.log(Level.INFO, "OK, environment ready");
        if ((args.length == 1) && args[0].equals("gui")) {
            this.model.setView(view);
        }
        //create a thread for each drone
        int DRONE_NUMBER = 3;
        executorService = Executors.newFixedThreadPool(DRONE_NUMBER); // 3 drones
        for (int i = 0; i < DRONE_NUMBER; i++) {
            String droneName = "drone" + (i + 1);
            DroneHandler handler = new DroneHandler(droneName, i, this, model, logger);
            executorService.submit(handler);
        }
        this.updatePizzeriaPercepts();
    }

    public synchronized void updatePercepts(String droneName, int droneId) {
        this.clearPercepts(droneName);
        //update perceptions
        Location lDrone = model.getAgPos(droneId);
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
        Location lRobot = model.getAgPos(ObjectsID.ROBOT.getId());
        this.addPercept(robotName, Literal.parseLiteral("current_position(" + lRobot.x + "," + lRobot.y + ")"));
    }

    @Override
    public synchronized boolean executeAction(final String ag, final Structure action) {
        boolean result = true;
        int agId = getAgIdBasedOnName(ag);
        if (action.getFunctor().equals("move")) {
            Term xTerm = action.getTerm(0);
            Term yTerm = action.getTerm(1);
            Term zTerm = action.getTerm(2);
            try {
                int x = Integer.parseInt(xTerm.toString());
                int y = Integer.parseInt(yTerm.toString());
                String mode = zTerm.toString();


                model.moveTowards(new Location(x, y), agId);
                if (ag.contains("drone")) { //if drone is moving
                    model.decreaseBatteryLevel(ag, mode); //reduce level depending on mode FULL or LOW
                }
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Errore in parsing coordinates", e);
                result = false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (action.getFunctor().equals("pizza_delivered")) {
            model.getPizzeria().removePizzas(1);
        } else if (action.getFunctor().equals("charge_drone")) {
            Term xTerm = action.getTerm(0);
            model.setBatteryLevel(xTerm.toString(), 100);
        } else if (action.getFunctor().equals("repair_drone")) {
            Term xTerm = action.getTerm(0);
            model.setBatteryLevel(xTerm.toString(), 100);
            model.repairDrone(xTerm.toString());
            model.setAgPos(getAgIdBasedOnName(xTerm.toString()), 26, 26);
        } else if (action.getFunctor().equals("break_drone")) {
            String droneName = action.getTerm(0).toString();
            if (Objects.equals(model.isDroneBroken(droneName), "no")) {
                model.setDroneBroken(droneName, "yes");
            }
        }
        this.updatePercepts(ag, agId);

        return result;
    }

    public int getAgIdBasedOnName(String agName) {
        return switch (agName) {
            case "drone1" -> 0;
            case "drone2" -> 1;
            case "drone3" -> 2;
            case "pizzeria" -> 3;
            case "robot" -> 4;
            default -> -1;
        };
    }

    @Override
    public void stop() {
        super.stop();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
