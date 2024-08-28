package env.objects.chargingBase;

import env.objects.ObjectsID;
import jason.environment.grid.Location;

public class ChargingBase {


    private Location location;
    private ChargingBaseStatus chargingBaseStatus;
    private int id;
    private String name;


    public ChargingBase(Location location, ChargingBaseStatus chargingBaseStatus, int id,String name) {
        this.location = location;
        this.chargingBaseStatus = chargingBaseStatus;
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ChargingBaseStatus getChargingBaseStatus() {
        return chargingBaseStatus;
    }

    public void setChargingBaseStatus(ChargingBaseStatus chargingBaseStatus) {
        this.chargingBaseStatus = chargingBaseStatus;
    }


    public int getId() {return this.id;}

    @Override
    public String toString() {
        return "ChargingBase{" +
                "location=" + location +
                ", chargingBaseStatus=" + chargingBaseStatus +
                '}';
    }
}
