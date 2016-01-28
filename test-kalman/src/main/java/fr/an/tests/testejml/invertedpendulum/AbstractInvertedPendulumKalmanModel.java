package fr.an.tests.testejml.invertedpendulum;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.tests.testejml.kalman.HybridKalmanFilter;

public abstract class AbstractInvertedPendulumKalmanModel {
    
    private static final Logger LOG = LoggerFactory.getLogger(AbstractInvertedPendulumKalmanModel.class);
    
    private int delay = 20; // millis;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Future<?> periodicUpdateTimer;
    
    private long timeMillis;

    protected HybridKalmanFilter kalmanFilter;
    
    protected InvertedPendulumParams params;
    protected InvertedPendulumModelMeasureSimulator modelMeasureSimulator;
    
    // ------------------------------------------------------------------------

    public AbstractInvertedPendulumKalmanModel(InvertedPendulumParams params, InvertedPendulumModelMeasureSimulator modelMeasureSimulator) {
        this.params = params;
        this.modelMeasureSimulator = modelMeasureSimulator;
        initKalmanModel();
    }

    // ------------------------------------------------------------------------
    
    public HybridKalmanFilter getKalmanFilter() {
        return kalmanFilter;
    }
    
    protected abstract void initKalmanModel();

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
        try {
            long currTimeMillis = System.currentTimeMillis();
            double dt = (currTimeMillis - timeMillis) * 0.001;
            modelMeasureSimulator.updateSimulatorNoiseMeasure(); // TODO may be externally updated... to compare several kalman filters with same noised data

            if (kalmanFilter.isValid()) {
                kalmanFilter.predictTimeStep(dt);
                
                simulateNoisedSensorMeasures();
            }

            
            timeMillis = currTimeMillis;
        } catch(Exception ex) {
            LOG.error("Failed", ex);
        }
    }

    /** simulate sensor measures with gaussian noise
     *  (with supposedly same noise gaussian param as kalmanFilter param R) 
     */ 
    protected abstract void simulateNoisedSensorMeasures();

    public abstract double getEstimatedPos();
    public abstract double getEstimatedAngle();

//    public abstract double getEstimatedVariancePos();
//    public abstract double getEstimatedVarianceAngle();
    
}
