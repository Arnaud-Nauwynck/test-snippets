package fr.an.tests.testejml.kalman;

import org.ejml.data.DenseMatrix64F;

/**
 * <p>
 * This is an interface for a discrete time Kalman filter with no control input:<br>
 * <br>
 * x<sub>k</sub> = F<sub>k</sub> x<sub>k-1</sub> + w<sub>k</sub><br>
 * z<sub>k</sub> = H<sub>k</sub> x<sub>k</sub> + v<sub>k</sub> <br>
 * <br>
 * w<sub>k</sub> ~ N(0,Q<sub>k</sub>)<br>
 * v<sub>k</sub> ~ N(0,R<sub>k</sub>)<br>
 * </p>
 *
 * @author Peter Abeles
 * 
 * 
 * example links
 * https://en.wikipedia.org/wiki/Rudolf_E._K%C3%A1lm%C3%A1n
 * http://www.cs.unc.edu/~welch/kalman/
 * http://www.cs.unc.edu/~welch/media/pdf/kalman_intro.pdf
 * https://github.com/search?l=Java&p=3&q=kalman+filter
 * https://github.com/raasun/cardboard/blob/master/src/com/google/vrtoolkit/cardboard/sensors/internal/OrientationEKF.java
 * https://github.com/googlecreativelab/landmarker/blob/master/app/src/main/java/com/androidexperiments/landmarker/sensors/HeadTracker.java
 * 
 * 
 * KF = Kalman Filter (linear)
 * EKF = Extended Kalman filter (non linear -> linearised)
 * UKF = Unscented Kalman filter (non linear -> ...) 
 *    http://www.control.aau.dk/~tb/ESIF/slides/ESIF_6.pdf
 * 
 * 
 * 
 */
public interface KalmanFilter {

    /**
     * Specify the kinematics model of the Kalman filter.  This must be called
     * first before any other functions.
     *
     * @param F State transition matrix.
     * @param Q plant noise.
     * @param H measurement projection matrix.
     */
    public void configure( DenseMatrix64F F, DenseMatrix64F Q ,
                           DenseMatrix64F H);

    /**
     * The prior state estimate and covariance.
     *
     * @param x The estimated system state.
     * @param P The covariance of the estimated system state.
     */
    public void setState( DenseMatrix64F x , DenseMatrix64F P );

    /**
     * Predicts the state of the system forward one time step.
     */
    public void predict();

    /**
     * Updates the state provided the observation from a sensor.
     *
     * @param z Measurement.
     * @param R Measurement covariance.
     */
    public void update( DenseMatrix64F z , DenseMatrix64F R );

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
}