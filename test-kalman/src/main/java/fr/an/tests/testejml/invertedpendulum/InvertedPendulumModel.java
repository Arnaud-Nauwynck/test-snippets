package fr.an.tests.testejml.invertedpendulum;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * example references:
 * http://ctms.engin.umich.edu/CTMS/index.php?example=InvertedPendulum&section=SystemModeling
 * http://ctms.engin.umich.edu/CTMS/index.php?example=InvertedPendulum&section=ControlStateSpace
 * http://www.cs.berkeley.edu/~pabbeel/cs287-fa09/readings/Tedrake-Aug09.pdf
 * http://publications.lib.chalmers.se/records/fulltext/99385.pdf
 * http://csuchico-dspace.calstate.edu/bitstream/handle/10211.4/145/4%2022%2009%20Jose%20Miranda.pdf?sequence=1Cuntsultriest
 * 
 */
public class InvertedPendulumModel {

    private static final double cstG = 9.8;
    
    // internal Model parameters
    public double paramCartMass = 1.;
    public double paramPoleMass = 0.1;
    public double paramPoleLength = 1.;
    public double fricPole = 0.005;
    // friction... not modeled in observable model..
    public double fricCart = 0.00005;

    // State
    private double pos, posDot, angle, angleDot;
    
    // Control State 
    // horizontal force to apply on vehicle, using normalized unit in [-1, 1]
    public double paramForceMag = 10.;
    private double controlForce = 0.0;
    
    private int delay = 20; // millis;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Future<?> periodicUpdateTimer;
    
    private long timeMillis;
    
    // ------------------------------------------------------------------------

    public InvertedPendulumModel() {
    }

    // ------------------------------------------------------------------------
    
    public void resetPole() {
        pos = 0.;
        posDot = 0.;
        angle = 0.;
        angleDot = 0.;
    }

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
        // Update the state of the pole;
        // First calc derivatives of state variables
        double force = paramForceMag * controlForce;
        double sinangle = Math.sin(angle);
        double cosangle = Math.cos(angle);
        double angleDotSq = angleDot * angleDot;
        double totalMass = paramCartMass + paramPoleMass;
        double halfPole = 0.5 * paramPoleLength;
        double poleMassLength = halfPole * paramPoleMass;

        double common = (force + poleMassLength * angleDotSq * sinangle - fricCart * (posDot < 0 ? -1 : 0)) / totalMass;
        double angleDDot = (cstG * sinangle - cosangle * common - fricPole * angleDot / poleMassLength)
            / (halfPole * (4. / 3. - paramPoleMass * cosangle * cosangle / totalMass));
        double posDDot = common - poleMassLength * angleDDot * cosangle / totalMass;

        // Now update state.
        pos += posDot * tau;
        posDot += posDDot * tau;
        angle += angleDot * tau;
        angleDot += angleDDot * tau;
    }
    
    public void setControlForce(double controlForce) {
        this.controlForce = controlForce;
    }

    public void incrControlForce(double incr) {
        this.controlForce += incr;
    }

    public double getControlForce() {
        return controlForce;
    }
    
    public double getPos() {
        return pos;
    }

    public double getPosDot() {
        return posDot;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngleDot() {
        return angleDot;
    }

    public double paramPoleLength() {
        return paramPoleLength;
    }

    public double getParamCartMass() {
        return paramCartMass;
    }

    public double getParamPoleMass() {
        return paramPoleMass;
    }

    public double getParamPoleLength() {
        return paramPoleLength;
    }

    public double getParamForceMag() {
        return paramForceMag;
    }

    
}
