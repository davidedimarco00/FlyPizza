package env.model.objects;

import jason.environment.grid.Location;

import static env.model.objects.EngineMode.LOW;

public class Drone {

    private Location location;
    private int batteryLevel;
    private String broken;
    private int ID;
    private String droneName;
    private EngineMode engineMode;


    public Drone(Location location, int ID, String droneName, String broken) {
        this.location = location;
        this.batteryLevel = 100; //all drone starts with 100% battery
        this.ID = ID;
        this.droneName = droneName;
        this.broken = broken;
        this.engineMode = EngineMode.LOW;
    }


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


    public String getBroken() {
        return broken;
    }

    public void setBroken(String broken) {
        this.broken = broken;
    }

    public EngineMode getEngineMode() {
        return this.engineMode;
    }

    public void setEngineMode(EngineMode engineMode) {
        this.engineMode = engineMode;
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
