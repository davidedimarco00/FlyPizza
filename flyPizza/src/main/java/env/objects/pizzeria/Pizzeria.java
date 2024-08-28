package env.objects.pizzeria;


import jason.environment.grid.Location;

public class Pizzeria {

    private final Location location;
    private final int MAX_PIZZAS = 80;
    private int numberOfPizzas = 40;
    private int id;



    public Pizzeria(Location location, int id) {
        this.location = location;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getNumberOfPizzas() {
        return numberOfPizzas;
    }

    public void addPizzas(int count) {
        this.numberOfPizzas += count;
    }

    public int removePizzas(int count) {

        if (count <= numberOfPizzas) {
            this.numberOfPizzas -= count;
            return this.numberOfPizzas;
        } else {
            return this.numberOfPizzas;
        }

    }

    @Override
    public String toString() {
        return "Pizzeria at " + location + " with " + numberOfPizzas + " pizzas available";
    }
}
