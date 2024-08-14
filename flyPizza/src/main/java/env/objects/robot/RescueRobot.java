package env.objects.robot;

import jason.environment.grid.Location;

import java.awt.*;

public class RescueRobot {


    private Location location;
    private int batteryLevel;
    private RobotStatus robotStatus;
    private int id;

    public RescueRobot(Location location, int batteryLevel, RobotStatus robotStatus, int id) {
        this.location = location;
        this.batteryLevel = batteryLevel;
        this.robotStatus = robotStatus;
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public RobotStatus getRobotStatus() {
        return robotStatus;
    }

    public void setRobotStatus(RobotStatus robotStatus) {
        this.robotStatus = robotStatus;
    }

    public int getId() {
        return id;
    }
}
