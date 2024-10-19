package env.model.behavior;

import env.FlyPizzaEnv;
import env.model.FlyPizzaModel;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DroneHandler implements Runnable {
    private final String droneName;
    private final int droneId;
    private final FlyPizzaEnv environment;
    private final FlyPizzaModel model;
    private final Logger logger;
    private final Random random;
    private final int failureCheckInterval;
    private final ScheduledExecutorService scheduler;

    public DroneHandler(String droneName, int droneId, FlyPizzaEnv environment, FlyPizzaModel model, Logger logger) {
        this.droneName = droneName;
        this.droneId = droneId;
        this.environment = environment;
        this.model = model;
        this.logger = logger;
        this.random = new Random();
        this.failureCheckInterval = 10000 + random.nextInt(5000);
        this.scheduler = Executors.newScheduledThreadPool(1); //schedulatore
    }

    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(() -> environment.updatePercepts(droneName, droneId), 0, 400, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(this::simulateRandomFailure, failureCheckInterval, failureCheckInterval, TimeUnit.MILLISECONDS);

    }

    private void simulateRandomFailure() {
        //genera un numero casuale tra 0 e 1
        double randomNumber = random.nextDouble();
        if (randomNumber < 0.25) { //indica la probabilità di rottura nel mio caso 0.25 = 25%
            if (Objects.equals(model.isDroneBroken(droneName), "no")) { //se non sono rotto allora mi rompo
                model.setDroneBroken(droneName, "yes");
                logger.info(droneName + " si è guastato");
            }
        }
    }
}
