package fr.an.tests.testejml.invertedpendulum;

import java.util.Random;

import org.ejml.data.DenseMatrix64F;
import static org.ejml.ops.CommonOps.*;

public class InvertedPendulumModelMeasureSimulator {

    private InvertedPendulumModel model;
    
    private Random randomGenerator = new Random();
    
    private DenseMatrix64F simulatorZ_XAngle;
    private DenseMatrix64F simulatorR_XAngle;
    private DenseMatrix64F simulatorV_XAngle;
    
    // ------------------------------------------------------------------------
    
    public InvertedPendulumModelMeasureSimulator(InvertedPendulumModel model) {
        this.model = model;
        simulatorZ_XAngle = new DenseMatrix64F(2,1);
        simulatorR_XAngle = new DenseMatrix64F(2,2);
        simulatorV_XAngle = new DenseMatrix64F(2,1);
        
        simulatorR_XAngle.setData(new double[] {
            0.00001, 0.00001, //
            0.00001, 0.00001
        });
    }
    
    // ------------------------------------------------------------------------

    public void updateSimulatorNoiseMeasure() {
        double realPos = model.getPos();
        double realAngle = model.getAngle();
        
        simulatorZ_XAngle.set(0, realPos);
        simulatorZ_XAngle.set(1, realAngle);
        simulatorV_XAngle.set(0, 0, randomGenerator.nextGaussian());
        simulatorV_XAngle.set(1, 0, randomGenerator.nextGaussian());        
        multAdd(simulatorR_XAngle, simulatorV_XAngle, simulatorZ_XAngle);
    }

    public DenseMatrix64F getSimulatorZ_XAngle() {
        return simulatorZ_XAngle;
    }
    
    public InvertedPendulumModel getModel() {
        return model;
    }
    
}
