package env;

import env.objects.ObjectsID;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.Location;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlyPizzaEnv extends Environment {

    static Logger logger = Logger.getLogger(FlyPizzaEnv.class.getName());
    final FlyPizzaModel model = new FlyPizzaModel();
    final FlyPizzaView view = new FlyPizzaView(model);

    private Location lDrone;

    /* Belief Literal */


    //Initial base

    /* Action Literal */
    public static final Literal moveAction = Literal.parseLiteral("move");
    public static final Literal pizzaDelivery = Literal.parseLiteral("deliverPizza");


    @Override
    public void init(final String[] args) {
        logger.log(Level.INFO, "OK, environment ready");
        if ((args.length == 1) && args[0].equals("gui")) {
            this.model.setView(view);
        }
        this.addInitialPerceptions();
        this.updatePercepts();
    }


    void addInitialPerceptions() {
        for (int i = 0; i < 3; i++) {
            String droneName = "drone" + (i + 1);
        }
    }


    void updatePercepts() {
        // Clear perceptions
        for (int i = 0; i < 3; i++) {
            String droneName = "drone" + (i + 1);
            this.clearPercepts(droneName);
            // Update drone perceptions
            this.lDrone = model.getAgPos(i);
            this.addPercept(droneName, Literal.parseLiteral("current_position("+lDrone.x + ","+lDrone.y + ")"));
        }
        this.clearPercepts(ObjectsID.PIZZERIA.getObjectStringName());
        this.clearPercepts(ObjectsID.ROBOT.getObjectStringName());
    }



    @Override
    public boolean executeAction(final String ag, final Structure action) {
        //logger.log(Level.INFO, "[" + ag + "] doing: " + action);

        boolean result = true;
        if (action.getFunctor().equals("move")) {
            int agId = getAgIdBasedOnName(ag);
            // Estrai le coordinate della destinazione
            Term xTerm = action.getTerm(0);
            Term yTerm = action.getTerm(1);
            try {
                int x = Integer.parseInt(xTerm.toString());
                int y = Integer.parseInt(yTerm.toString());
                model.moveTowards(new Location(x,y), agId);
                //this.addPercept("drone"+String.valueOf(agId), Literal.parseLiteral("current_position("+lDrone.x + ","+lDrone.y + ")"));
            } catch (NumberFormatException e) {
                logger.log(Level.SEVERE, "Error parsing coordinates", e);
                result = false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        else if (action.getFunctor().equals("pizza_delivered")) {
            //logger.log(Level.INFO, "Ho deliverato una pizza");
            this.model.getPizzeria().removePizzas(1);
        }


        this.slowDownSystem();
        this.updatePercepts();
        return result;
    }

    private void slowDownSystem() {
        try {
            Thread.sleep(200L); //slow down the system
        } catch (InterruptedException ignored) {

        }
    }

    private int getAgIdBasedOnName(String agName) {
        switch (agName) {
            case "drone1": return 0;
            case "drone2": return 1;
            case "drone3": return 2;
            case "pizzeria": return 3;
            case "robot": return 4;
            default: return -1;
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}
