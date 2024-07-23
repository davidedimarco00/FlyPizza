package env;

import jason.NoValueException;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Structure;
import jason.environment.Environment;
import jason.environment.grid.Location;

import java.util.logging.Logger;



public class FlyPizzaEnv extends Environment {

    @Override
    public void init(final String[] args) {
       System.out.println("Ok, FlyPizza Environment ready to start! 🍕✈️");
    }


    void updatePercepts(String ag) {

    
    }

    @Override
    public boolean executeAction(final String ag, final Structure action) {
       return false;
    }


}
