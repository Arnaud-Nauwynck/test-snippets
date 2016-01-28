package fr.an.tests.testejml.kalman;

import org.ejml.data.DenseMatrix64F;

/**
 * <p>
 * This is an interface for a hybrid Kalman filter (= continuous time state / discrete time measures) 
 * with control input and several possible measures set<br>
 * <br>
 * dx/dt<sub>t</sub> = F<sub>k</sub> x<sub>t</sub> + w<sub>t</sub><br>
 * z1<sub>k</sub> = H1<sub>k</sub> x<sub>k</sub> + v1<sub>k</sub> <br>
 * z2<sub>k</sub> = H2<sub>k</sub> x<sub>k</sub> + v2<sub>k</sub> <br>
 * ..
 * zm<sub>k</sub> = Hm<sub>k</sub> x<sub>k</sub> + vm<sub>k</sub> <br>
 * <br>
 * w<sub>k</sub> ~ N(0,Q<sub>k</sub>)<br>
 * vm<sub>k</sub> ~ N(0,Rm<sub>k</sub>)<br>
 * </p>
 * 
 * Measure Sets can come at different frequencies / async interruptions depending on sensors (raw electronic sensor, reception of radar signal, video recognition, ...) 
 * 
 * 
 */
public interface HybridKalmanFilter {

    public void initStateTransition(int dimX, int dimU, int dimW);

    public int initNthMeasureTransition(String measureSetName, int dimZ, int dimV);
    
    /**
     * Specify the kinematics model of the Kalman filter.  This must be called
     * first before any other functions.
     *
     * @param F State transition matrix.
     * @param G Control transition matrix.
     * @param Q plant noise.
     * @param H measurement projection matrix.
     */
    public void setStateTransition( DenseMatrix64F F, DenseMatrix64F G, DenseMatrix64F Q);

    public void setNthMeasureTransition(int measureSetIndex, DenseMatrix64F H, DenseMatrix64F R);
    
    /**
     * The prior state estimate and covariance.
     *
     * @param x The estimated system state.
     * @param P The covariance of the estimated system state.
     */
    public void setState( DenseMatrix64F x , DenseMatrix64F P );

    /**
     * @param u the command
     */
    public void setControlCommand( DenseMatrix64F u );
    
    /**
     * Predicts the state of the system forward one time step.
     */
    public void predictTimeStep(double dt);

    /**
     * Updates the state provided the observation from a sensor.
     *
     * @param z Measurement.
     * @param R Measurement covariance.
     */
    public void updateNthMeasure(int measureSetIndex, DenseMatrix64F z);

    /**
     * Returns the current estimated state of the system.
     *
     * @return The state.
     */
    public DenseMatrix64F getState();

    /**
     * Returns the estimated state's covariance matrix.
     *
     * @return The covariance.
     */
    public DenseMatrix64F getCovariance();

    public boolean isValid();
}