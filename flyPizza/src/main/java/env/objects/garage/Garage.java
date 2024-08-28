package env.objects.garage;

import jason.environment.grid.Location;

public class Garage {


    private Location location;
    private int id;
    private final int TOTAL_PARKING = 5;
    private int availableParking = 5; //il garage ha 5 posti per i 5 droni

    private boolean[] occupiedList = {false, false, false};


    public Garage(Location location, int id) {
        this.location = location;
        this.id = id;
    }


    public void setParkingOccupied(int index, boolean value) {
        occupiedList[index] = value;
    }

    public int getAvailableParking() {
        return this.availableParking;
    }

    public void setAvailableParking(int availableParking) {
        this.availableParking = availableParking;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location, int id) {
        this.location = location;
    }

    public int getId() {return this.id;}

    @Override
    public String toString() {
        return "Garage{" +
                "location=" + location +
                ", availableParking=" + availableParking +
                '}';
    }

    public int getNumberOfParkedDrones() {
        return this.TOTAL_PARKING - this.availableParking;
    }
}
