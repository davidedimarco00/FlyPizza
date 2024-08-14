package env.objects.drone;

import jason.environment.grid.Location;

public class Drone {

    private Location location;
    private int batteryLevel;
    private DroneStatus droneStatus;
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

    public DroneStatus getDroneStatus() {
        return droneStatus;
    }

    public void setDroneStatus(DroneStatus droneStatus) {
        this.droneStatus = droneStatus;
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

    public EnginePowerMode getEnginePowerMode() {
        return enginePowerMode;
    }

    public void setEnginePowerMode(EnginePowerMode enginePowerMode) {
        this.enginePowerMode = enginePowerMode;
    }

    private String droneName;
    private EnginePowerMode enginePowerMode;

    public Drone(Location location, DroneStatus droneStatus, int ID, String droneName, EnginePowerMode enginePowerMode) {
        this.location = location;
        this.batteryLevel = 100; //all drone starts with 100% battery
        this.droneStatus = droneStatus;
        this.ID = ID;
        this.droneName = droneName;
        this.enginePowerMode = enginePowerMode;
    }


    @Override
    public String toString() {
        return "Drone{" +
                "location=" + location +
                ", energyLevel=" + batteryLevel +
                ", droneStatus=" + droneStatus +
                ", ID=" + ID +
                ", droneName='" + droneName + '\'' +
                '}';
    }
}
