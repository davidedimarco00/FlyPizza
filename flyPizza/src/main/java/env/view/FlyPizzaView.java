package env.view;

import env.model.FlyPizzaModel;
import env.model.objects.ObjectsID;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Set;
import javax.swing.SwingUtilities;

public class FlyPizzaView extends GridWorldView {
    private final FlyPizzaModel model;

    public FlyPizzaView(final FlyPizzaModel model) {
        super(model, "FlyPizza", 1000);
        this.model = model;
        this.defaultFont = new Font("Verdana", Font.BOLD, 13);
        SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

    @Override
    public void draw(final Graphics g, final int x, final int y, final int object) {
        //disegna gli ostacoli
        drawObstacles(g);
        //disegna l'oggetto in base al suo ID
        if (ObjectsID.fromValue(object) == ObjectsID.PIZZERIA) {
            drawPizzeria(g, x, y);
        }
    }

    private void drawObstacles(final Graphics g) {
        Set<Location> obstacles = model.getObstacles();
        obstacles.forEach(loc -> drawObstacle(g, loc.x, loc.y));
    }

    private void drawPizzeria(final Graphics g, final int x, final int y) {
        g.setColor(Color.RED);
        super.drawAgent(g, x, y, Color.RED, -1);
        g.setColor(Color.BLACK);
        drawString(g, x, y + 1, this.defaultFont, "Pizzeria (" + model.getPizzeria().getNumberOfPizzas() + ")");
    }

    @Override
    public void drawAgent(final Graphics g, final int x, final int y, Color c, final int id) {
        if (id == ObjectsID.ROBOT.getId()) {
            c = Color.ORANGE;
        } else {
            c = getDroneColor(id);
        }

        if (!isSpecialLocation(model.getAgPos(id))) {
            super.drawAgent(g, x, y, c, id);
        }

        // Se il drone è alla pizzeria, disegna informazioni aggiuntive
        if (model.getPizzeria().getLocation().equals(model.getAgPos(id))) {
            drawString(g, x, y + 1, this.defaultFont, "Pizzeria (" + model.getPizzeria().getNumberOfPizzas() + ")");
        }
    }

    private boolean isSpecialLocation(final Location loc) {
        return loc.equals(model.getPizzeria().getLocation()) || loc.equals(model.getRobot().getLocation());
    }

    private Color getDroneColor(final int id) {
        switch (id) {
            case 0: return Color.GREEN;   //drone 1
            case 1: return Color.MAGENTA; //drone 2
            case 2: return Color.CYAN;    //drone 3
            default: return Color.GRAY;   //colore di default
        }
    }
}
