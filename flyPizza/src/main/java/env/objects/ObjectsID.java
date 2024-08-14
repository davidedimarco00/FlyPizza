package env.objects;

public enum ObjectsID {
    CHARGING_BASE1(16, "Base1", 0),
    CHARGING_BASE2(32, "Base2", 1),
    CHARGING_BASE3(64, "Base3", 2),
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