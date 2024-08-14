package env;

import env.objects.ObjectsID;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.Environment;
import jason.environment.grid.Location;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlyPizzaEnv extends Environment {

    static Logger logger = Logger.getLogger(FlyPizzaEnv.class.getName());
    final FlyPizzaModel model = new FlyPizzaModel();
    final FlyPizzaView view = new FlyPizzaView(model);


    /**/

    /*Action Literal*/
    public static final Literal moveAction = Literal.parseLiteral("move");
    public static final Literal pizzaDelivery = Literal.parseLiteral("pizza_delivered");


    /*Belief Literal*/





    @Override
    public void init(final String[] args) {
        logger.log(Level.INFO, "OK, environment ready");
        if ((args.length == 1) && args[0].equals("gui")) {
            this.model.setView(view);
        }
        this.updatePercepts();
    }

    void updatePercepts() {

        //Clear perceptions
        for (int i = 1; i <= 5; i++) {
            String droneName = "drone" + i;
            this.clearPercepts(droneName);
        }
        this.clearPercepts(ObjectsID.PIZZERIA.getObjectStringName());
        this.clearPercepts(ObjectsID.ROBOT.getObjectStringName());

        //Update drone perceptions
        for (int i = 0; i < 5; i++) {
            Location lDrone = model.getAgPos(i);
            String droneName = "drone" + (i + 1);
            addPercept(droneName, Literal.parseLiteral("location(" + lDrone.x + "," + lDrone.y + ")"));
        }

        //addPercept(Literal.parseLiteral(String.format("numberOfAvailablePizzas(%s)", model.numberOfAvailablePizzas)));


    }

   @Override
    public Collection<Literal> getPercepts(String agName) {
        return Collections.singletonList(
                Literal.parseLiteral(String.format("numberOfAvailablePizzas(%s)", model.numberOfAvailablePizzas))
        );
    }

    @Override
    public boolean executeAction(final String ag, final Structure action) {
       logger.log(Level.INFO, "[" + ag + "] doing: " + action);

       boolean result = true;


        if (action.equals(moveAction)) {
            int agId = getAgIdBasedOnName(ag);
            //model.moveRandomly(agId);


        } else if (action.equals(pizzaDelivery)) {

            //model.decreasePizza();


        }

        try {
            Thread.sleep(500L);
        } catch (InterruptedException ignored) {
        }
        updatePercepts();
        return result;
    }

    private int getAgIdBasedOnName(String agName) {
        switch (agName) {
            case "drone1": return 0;
            case "drone2": return 1;
            case "drone3": return 2;
            case "drone4": return 3;
            case "drone5": return 4;
            case "pizzeria": return 5;
            case "robot": return 6;
            default: return -1;
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}




