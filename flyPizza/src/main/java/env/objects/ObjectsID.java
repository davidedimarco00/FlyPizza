package env.objects;

public enum ObjectsID {
    PIZZERIA(32, "Pizzeria", 3),
    ROBOT(64, "Robot", 4),
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