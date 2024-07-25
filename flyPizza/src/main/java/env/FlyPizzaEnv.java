package env;

import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.Location;
import java.util.logging.Logger;

/**
 * Any Jason environment "entry point" should extend
 * jason.environment.Environment class to override methods init(),
 * updatePercepts() and executeAction().
 */
public class FlyPizzaEnv extends Environment {

   

    static Logger logger = Logger.getLogger(FlyPizzaEnv.class.getName());

    FlyPizzaModel model; // the model of the grid

    @Override
    public void init(final String[] args) {
        this.model = new FlyPizzaModel();

        if ((args.length == 1) && args[0].equals("gui")) {
            final FlyPizzaView view = new FlyPizzaView(this.model);
            this.model.setView(view);
        }
   
        this.updatePercepts();
    }

    
    void updatePercepts() {
       

      
    }

    
    @Override
    public boolean executeAction(final String ag, final Structure action) {
       
        return false;
    }
}
