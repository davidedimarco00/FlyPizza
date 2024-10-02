package utils;

import env.FlyPizzaModel;
import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.JasonException;
import java.util.Random;
import java.util.Set;

import jason.environment.grid.Location;


public class random_destination extends DefaultInternalAction {
    private Random randomGenerator = new Random();
    private final Set<Location> occupiedLocations = FlyPizzaModel.getOccupiedLocations();

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // Verifica che ci siano abbastanza argomenti
        if (args.length != 3) {
            throw new JasonException("Il predicato random_destination richiede tre argomenti: il massimo valore del range e due variabili per i risultati.");
        }
        int maxRange = (int) ((NumberTerm) args[0]).solve();
        int randomValueX;
        int randomValueY;
        Location randomLocation;

        // Continua a generare numeri casuali finché non ottieni una posizione libera
        do {
            randomValueX = randomGenerator.nextInt(maxRange);
            randomValueY = randomGenerator.nextInt(maxRange);
            randomLocation = new Location(randomValueX, randomValueY);
        } while (occupiedLocations.contains(randomLocation)); // evito di estrarre delle destinazioni che sono gia occupate da altri oggetti

        Term resultX = new NumberTermImpl(randomValueX);
        Term resultY = new NumberTermImpl(randomValueY);

        boolean successX = un.unifies(resultX, args[1]);
        boolean successY = un.unifies(resultY, args[2]);
        return successX && successY;
    }
}
