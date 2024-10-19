package env.model.objects;

import jason.environment.grid.Location;

public class Robot {


    private Location location;
    private int id;

    public Robot(Location location, int id) {
        this.location = location;
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public int getId() {
        return id;
    }
}
