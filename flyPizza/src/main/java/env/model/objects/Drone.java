package env.model.objects;

import jason.environment.grid.Location;

public class Drone {

    private Location location;
    private int batteryLevel;
    private int ID;
    private String name;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location, int ID) {
        this.location = location;
        this.ID = ID;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getID() {
        return ID;
    }

    public String getDroneName() {
        return droneName;
    }

    public void setDroneName(String droneName) {
        this.droneName = droneName;
    }

    private String droneName;

    public Drone(Location location, int ID, String droneName) {
        this.location = location;
        this.batteryLevel = 100; //all drone starts with 100% battery
        this.ID = ID;
        this.droneName = droneName;
    }


    @Override
    public String toString() {
        return "Drone{" +
                "location=" + location +
                ", energyLevel=" + batteryLevel +
                ", ID=" + ID +
                ", droneName='" + droneName + '\'' +
                '}';
    }


    





}
