package env.objects.drone;

public enum EnginePowerMode {
    FULL("FULL", 0.8),
    LOW("LOW", 0.4);

    private String mode;
    private double batteryDrainRate;

    EnginePowerMode(String mode, double batteryDrainRate) {
        this.mode = mode;
        this.batteryDrainRate = batteryDrainRate;
    }

    public String getMode() {
        return mode;
    }

    public double getBatteryDrainRate() {
        return this.batteryDrainRate;
    }


    @Override
    public String toString() {
        return "EnginePowerMode{" +
                "mode='" + mode + '\'' +
                ", batteryDrainRate=" + batteryDrainRate +
                '}';
    }
}

