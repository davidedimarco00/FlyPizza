package env;

import env.objects.ObjectsID;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Set;
import javax.swing.SwingUtilities;

public class FlyPizzaView extends GridWorldView {
    FlyPizzaModel model;

    public FlyPizzaView(final FlyPizzaModel model) {
        super(model, "FlyPizza", 1000);
        this.model = model;
        this.defaultFont = new Font("Verdana", Font.BOLD, 13); // change default font
        SwingUtilities.invokeLater(() -> {
            this.setVisible(true);
            this.repaint();
        });

    }

    private static Location copyOf(final Location l) {
        return new Location(l.x, l.y);
    }

    @Override
    public void draw(final Graphics g, final int x, final int y, final int object) {

        // Disegna gli ostacoli
        Set<Location> locationSet = this.model.getObstacles();
        locationSet.forEach(t -> {
            this.drawObstacle(g, t.x, t.y);
        });

        // Ottiene l'ID dell'oggetto e lo disegna nella griglia
        ObjectsID id = ObjectsID.fromValue(object);

        switch (id) {
            case PIZZERIA:
                g.setColor(Color.RED);
                super.drawAgent(g, x, y, Color.RED, -1);
                g.setColor(Color.BLACK);
                if ( this.model.getPizzeria().getNumberOfPizzas()  <= 0) {
                    this.drawString(g, x, y+1, this.defaultFont, "Pizze finite!!!!");
                }
                this.drawString(g, x, y+1, this.defaultFont, "Pizzeria (" + this.model.getPizzeria().getNumberOfPizzas() + ")");
                break;
        }
    }

    @Override
    public void drawAgent(final Graphics g, final int x, final int y, Color c, final int id) {
        Location loc = model.getAgPos(id);
        if (id == 4) { //id del robot
            c = Color.ORANGE;
            super.drawAgent(g, x, y, c, id);
        } else {
            // Verifica se la posizione è una posizione speciale
            boolean isSpecialLocation = loc.equals(model.getPizzeria().getLocation()) || loc.equals(model.getRobot().getLocation());

            if (!isSpecialLocation) {
                c = Color.BLACK;
                super.drawAgent(g, x, y, c, id);
            }

            if (this.model.getPizzeria().getLocation().equals(loc)) {
                this.drawString(g, x, y+1, this.defaultFont, "Pizzeria (" + this.model.getPizzeria().getNumberOfPizzas() + ")");
            }
        }
    }


}
