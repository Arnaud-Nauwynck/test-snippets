package fr.an.tests.testejml.invertedpendulum;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class InvertedPendulumKalmanModel {

    private int delay = 20; // millis;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Future<?> periodicUpdateTimer;
    
    private long timeMillis;

    // ------------------------------------------------------------------------

    public InvertedPendulumKalmanModel() {
    }

    // ------------------------------------------------------------------------

    public void start() {
        if (periodicUpdateTimer == null) {
            timeMillis = System.currentTimeMillis();
            periodicUpdateTimer = executorService.scheduleWithFixedDelay(() -> onUpdateTime(), 
                0, delay, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (periodicUpdateTimer != null) {
            periodicUpdateTimer.cancel(false);
            periodicUpdateTimer = null;
        }
    }
    
    public void onUpdateTime() {
        long currTimeMillis = System.currentTimeMillis();
        double tau = (currTimeMillis - timeMillis) / delay * 0.001;
        
        updateTime(tau);
        
        timeMillis = currTimeMillis;
    }

    public void updateTime(double tau) {
        
    }

}
