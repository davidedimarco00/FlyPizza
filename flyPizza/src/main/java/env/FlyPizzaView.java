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
        this.defaultFont = new Font("Helvetica", Font.BOLD, 14); // change default font
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

        //draw obstacles
        Set<Location> locationSet = this.model.getObstacles();
        locationSet.forEach(t -> {
            this.drawObstacle(g, t.x, t.y);
        });
        //get object ids and print them in the grid
        ObjectsID id = ObjectsID.fromValue(object);


        switch (id) {
            case CHARGING_BASE1, CHARGING_BASE3, CHARGING_BASE2:
                g.setColor(Color.blue);
                this.drawString(g, x, y + 1, this.defaultFont, id.getObjectStringName());
                super.drawAgent(g, x, y, Color.blue, -1);
                break;
            case PIZZERIA:
                g.setColor(Color.RED);

                super.drawAgent(g, x, y, Color.red, -1);
                break;
            case ROBOT:
                g.setColor(Color.ORANGE);
                //this.drawString(g, x + 3, y, this.defaultFont, "Robot (" + this.model.robotBatteryLevel + ")");
                //super.drawAgent(g, x, y, Color.ORANGE, -1);
                break;

            default:
                break;
        }

    }

    @Override
    public void drawAgent(final Graphics g, final int x, final int y, Color c, final int id) {
        Location loc = model.getAgPos(id);

        if (id == 4) {
            c = Color.ORANGE;
            super.drawAgent(g, x, y, c, id);
        } else if (!loc.equals(this.model.getChargingBases().get(0).getLocation()) &&
                !loc.equals(this.model.getChargingBases().get(1).getLocation()) &&
                !loc.equals(this.model.getChargingBases().get(2).getLocation()) &&
                !loc.equals(this.model.getPizzeria().getLocation()) &&
                !loc.equals(this.model.getRobot().getLocation())) {

            c = Color.BLACK;
            super.drawAgent(g, x, y, c, id);
        } else {

            super.drawAgent(g, x, y, c, id);
        }
    }


}
