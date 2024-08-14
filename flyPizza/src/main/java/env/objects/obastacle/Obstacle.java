package env.objects.obastacle;

import jason.environment.grid.Location;

import java.awt.*;

public class Obstacle {
    private Location location;
    private int height;
    private int width;


    public Obstacle(Location location, int height, int width) {
        this.location = location;
        this.height = height;
        this.width = width;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return "Obstacle{" +
                "location=" + location +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
