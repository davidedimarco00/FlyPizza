package utils;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;

import java.util.Random;

public class random_number extends DefaultInternalAction {

    private Random randomGenerator = new Random();


    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // Verifica che ci siano abbastanza argomenti
        if (args.length != 2) {
            throw new JasonException("Il predicato .my_random richiede due argomenti.");
        }
        int maxRange = (int) ((NumberTerm) args[0]).solve();

        // Genera il numero casuale
        int randomValue = randomGenerator.nextInt(maxRange);
        Term result = new NumberTermImpl(randomValue);
        return un.unifies(result, args[1]);
    }
}