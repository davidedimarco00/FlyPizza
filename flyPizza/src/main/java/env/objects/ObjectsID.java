package env.objects;

public enum ObjectsID {
    PIZZERIA(128, "Pizzeria", 3),
    GARAGE(256, "Garage", 4),
    ROBOT(512, "Robot", 5),
    DRONE(1024, "Drone", 6);


    private final int value;
    private final String objectStringName;
    private final int id;

    ObjectsID(int value, String objectStringName, int id) {
        this.value = value;
        this.objectStringName = objectStringName;
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public String getObjectStringName() {
        return this.objectStringName;
    }

    public int getId() {return this.id;}

    public static ObjectsID fromValue(int value) {
        for (ObjectsID id : ObjectsID.values()) {
            if (id.getValue() == value) {
                return id;
            }
        }
        throw new IllegalArgumentException("Invalid object ID: " + value);
    }
}