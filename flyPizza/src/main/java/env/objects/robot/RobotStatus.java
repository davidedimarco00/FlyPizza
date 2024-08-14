package env.objects.robot;

import java.awt.*;

public enum RobotStatus {

    RESCUING(Color.red), //is rescuing someone
    UNAVAILABLE(Color.blue), //is low in battery charge
    BROKEN(Color.YELLOW), //is broken
    AVAILABLE(Color.green); //is ok, it's ready to rescu

    private final Color color;


    RobotStatus(Color color) {
        this.color = color;
    }

    public Color getColor() {return this.color;

    }


}
