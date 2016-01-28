package fr.an.tests.testejml.invertedpendulum;

import org.ejml.data.DenseMatrix64F;

import fr.an.tests.testejml.kalman.JEMLEqHybridKalmanFilter;

public class SimpleInvertedPendulumKalmanFilter extends AbstractInvertedPendulumKalmanModel {

    private int indexMeasureXAngle;
    
    protected static final int STATE_DOTPOS = 0;
    protected static final int STATE_POS = 1;
    protected static final int STATE_DOTANGLE = 2;
    protected static final int STATE_ANGLE = 3;
    
    // ------------------------------------------------------------------------
    
    public SimpleInvertedPendulumKalmanFilter(InvertedPendulumParams modelParams, InvertedPendulumModelMeasureSimulator modelSimulator) {
        super(modelParams, modelSimulator);
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    protected void initKalmanModel() {
        this.kalmanFilter = new JEMLEqHybridKalmanFilter();
        this.kalmanFilter.initStateTransition(4, 1, 2);
        indexMeasureXAngle = kalmanFilter.initNthMeasureTransition("XAngle", 2, 2);
        
        updateLinearize();
    }
    
    protected void updateLinearize() {
        DenseMatrix64F x = kalmanFilter.getState();
        
        // TODO NOT IMPLEMENTED YET !!
        
//        // Update the state of the pole;
//        // First calc derivatives of state variables
//        double sinangle = Math.sin(angle);
//        double cosangle = Math.cos(angle);
//        double angleDotSq = angleDot * angleDot;
//        
//        double paramCartMass  = params.getParamCartMass();
//        double paramPoleMass = params.getParamPoleMass();
//        double totalMass = paramCartMass + paramPoleMass;
//        double halfPole = 0.5 * params.getParamPoleLength();
//        double poleMassLength = halfPole * paramPoleMass;
//        double fricCart = params.fricCart;
//        double fricPole = params.fricPole;
//        
//        double common = (controlForce + poleMassLength * angleDotSq * sinangle 
//                - fricCart * (posDot < 0 ? -1 : 0)) / totalMass;
//        double angleDDot = (cstG * sinangle - cosangle * common - fricPole * angleDot / poleMassLength)
//            / (halfPole * (4. / 3. - paramPoleMass * cosangle * cosangle / totalMass));
//        double posDDot = common - poleMassLength * angleDDot * cosangle / totalMass;
//
//        // Now update state.
//        posDot += posDDot * dt;
//        pos += posDot * dt;
//        angleDot += angleDDot * dt;
//        angle += angleDot * dt;
    }

    @Override
    protected void simulateNoisedSensorMeasures() {
        DenseMatrix64F simulatorZ_XAngle = modelMeasureSimulator.getSimulatorZ_XAngle();
        kalmanFilter.updateNthMeasure(indexMeasureXAngle, simulatorZ_XAngle);
        
        // (optionnaly?) re-linearize each time...
        updateLinearize();
    }

    @Override
    public double getEstimatedPos() {
        return kalmanFilter.getState().get(STATE_POS);
    }
    
    @Override
    public double getEstimatedAngle() {
        return kalmanFilter.getState().get(STATE_ANGLE);
    }

}
