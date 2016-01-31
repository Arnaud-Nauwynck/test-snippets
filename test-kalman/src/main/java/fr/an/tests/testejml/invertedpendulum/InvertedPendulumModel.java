package fr.an.tests.testejml.invertedpendulum;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ejml.data.DenseMatrix64F;

/**
 * example references:
 * http://ctms.engin.umich.edu/CTMS/index.php?example=InvertedPendulum&section=SystemModeling
 * http://ctms.engin.umich.edu/CTMS/index.php?example=InvertedPendulum&section=ControlStateSpace
 * http://www.cs.berkeley.edu/~pabbeel/cs287-fa09/readings/Tedrake-Aug09.pdf
 * http://publications.lib.chalmers.se/records/fulltext/99385.pdf
 * http://csuchico-dspace.calstate.edu/bitstream/handle/10211.4/145/4%2022%2009%20Jose%20Miranda.pdf?sequence=1Cuntsultriest
 * http://www.math.iisc.ernet.in/~ifcam/pendulum.pdf
 * http://www.profjrwhite.com/system_dynamics/sdyn/s7/s7invp2/s7invp2.html
 * http://pytrajectory.readthedocs.org/en/master/guide/examples/inv_pendulum_trans.html
 * http://robotics.ee.uwa.edu.au/theses/2003-Balance-Ooi.pdf
 * 
 */
public class InvertedPendulumModel {

    private static final double cstG = 9.8;
    
    // internal Model parameters
    private InvertedPendulumParams params;

    // State
    private double pos, posDot, angle, angleDot;
    private DenseMatrix64F state = new DenseMatrix64F(4, 1);
    
    // Control State 
    // horizontal force to apply on vehicle, using normalized unit in [-1, 1]
    private double controlForce = 0.0;
    
    private int delay = 5; // millis;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private Future<?> periodicUpdateTimer;
    
    private long timeMillis;
    
    // ------------------------------------------------------------------------

    public InvertedPendulumModel(InvertedPendulumParams params) {
        this.params = params;
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
    
    public void updateTime(double dt) {
        // Update the state of the pole;
        // First calc derivatives of state variables
        double sinangle = Math.sin(angle);
        double cosangle = Math.cos(angle);
        double angleDotSq = angleDot * angleDot;
        
        double paramCartMass  = params.getParamCartMass();
        double paramPoleMass = params.getParamPoleMass();
        double totalMass = paramCartMass + paramPoleMass;
        double halfPole = 0.5 * params.getParamPoleLength();
        double poleMassLength = halfPole * paramPoleMass;
        double fricCart = params.fricCart;
        double fricPole = params.fricPole;
        
        double common = (controlForce + poleMassLength * angleDotSq * sinangle 
                - fricCart * (posDot < 0 ? -1 : 0)
                ) / totalMass;
        double angleDDot = (cstG * sinangle - cosangle * common - fricPole * angleDot / poleMassLength)
            / (halfPole * (4. / 3. - paramPoleMass * cosangle * cosangle / totalMass));
        double posDDot = common - poleMassLength * angleDDot * cosangle / totalMass;

        // Now update state.
        pos += posDot * dt;
        posDot += posDDot * dt;
        angle += angleDot * dt;
        angleDot += angleDDot * dt;
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

    public InvertedPendulumParams getParams() {
        return params;
    }

    public DenseMatrix64F getState() {
        state.set(0, posDot);
        state.set(1, pos);
        state.set(2, angleDot);
        state.set(3, angle);
        return state;
    }
    
}
