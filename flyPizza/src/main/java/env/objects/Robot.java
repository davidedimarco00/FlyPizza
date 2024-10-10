package env.objects;

import jason.environment.grid.Location;

public class Robot {


    private Location location;
    private int id;

    public Robot(Location location, int batteryLevel, int id) {
        this.location = location;
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }
}
