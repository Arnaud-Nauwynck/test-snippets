package fr.an.tests.testejml.invertedpendulum;

import java.text.DecimalFormat;

import org.ejml.data.DenseMatrix64F;
import org.ejml.simple.SimpleMatrix;
import org.junit.Test;

import fr.an.tests.testejml.kalman.HybridKalmanFilter;
import fr.an.tests.testejml.util.DenseMatrix64FUtils;

public class SimpleInvertedPendulumKalmanFilterTest {
    private static final double PREC = 1e-7;

    
    @Test
    public void testLinearize_0() {
        InvertedPendulumParams modelParams = new InvertedPendulumParams();
        modelParams.setParamCartMass(0.5);
        modelParams.setParamPoleMass(0.2);
        modelParams.setFricCart(0.1);
        // modelParams.setInertia(0.006);
        modelParams.setParamPoleLength(0.3);
        
        SimpleInvertedPendulumKalmanFilter sut = new SimpleInvertedPendulumKalmanFilter(modelParams, null);

        HybridKalmanFilter kf = sut.getKalmanFilter();
        
        DenseMatrix64F F = kf.getF();
        System.out.println("F=\n" + DenseMatrix64FUtils.matToString(F, null));
        
        final double epsAngle = 3.14/100;
        for(int i_angle = -100; i_angle < 100; i_angle++) {
            double angle0 = i_angle * epsAngle; 
            
            DenseMatrix64F x0 = new DenseMatrix64F(4, 1);
            x0.set(3, angle0);
            
            doTestLinearizeAtX0(sut, x0);
        }
        
    }


    private void doTestLinearizeAtX0(SimpleInvertedPendulumKalmanFilter sut,  
            DenseMatrix64F x0) {
        final double dt = 1e-4;
        final double eps = 1e-4;
        final double prec = 1e-5;
        HybridKalmanFilter kf = sut.getKalmanFilter();

        DenseMatrix64F x1 = new DenseMatrix64F(4, 1); 
        DenseMatrix64F P = new DenseMatrix64F(4, 4); 

        DenseMatrix64F predictLinear = new DenseMatrix64F(4, 1); 
        DenseMatrix64F predictNonLinear = new DenseMatrix64F(4, 1); 
        
        kf.setState(x0, P);
        sut.updateLinearize();

        DenseMatrix64F x = new DenseMatrix64F(4, 1); 
        for (int i = 0; i < 4; i++) {
            x1.set(x0);
            x1.set(i, x.get(i) + eps);
     
            // compare nonLinear and linear result ... 
            sut.setUseEKFNonLinear(true);
            x.set(x1);
            kf.setState(x, P);
            kf.predictTimeStep(dt);
            predictLinear.set(kf.getState());
            
            // check using linear x=x+F(x-x0)
            sut.setUseEKFNonLinear(false);
            x.set(x1);
            kf.setState(x, P);
            kf.predictTimeStep(dt);
            predictNonLinear.set(kf.getState());
            
            assertEquals(predictLinear, predictNonLinear, prec);
        }
    }


    private void assertEquals(DenseMatrix64F expected, DenseMatrix64F actual, double prec) {
        for(int i = 0; i < actual.getNumRows(); i++) {
            for (int j = 0; j < actual.getNumCols(); j++) {
                double expValue = expected.get(i, j);
                double actValue = actual.get(i, j);
                if (Math.abs(expValue - actValue) > prec) {
                    DecimalFormat df = new DecimalFormat("0.##E0");
                    DenseMatrix64F diff = SimpleMatrix.wrap(expected).minus(SimpleMatrix.wrap(actual)).getMatrix();
                    throw new AssertionError("expected [" + i + "," + j + "] " + expValue + ", got :" + actValue + "\n"
                        + "expected: " + DenseMatrix64FUtils.matOrVectToString(expected, df) + "\n"
                        + "actual  : " + DenseMatrix64FUtils.matOrVectToString(actual, df) + "\n"
                        + "diff    : " + DenseMatrix64FUtils.matOrVectToString(diff, df));
                }
            }
        }
    }
    
}
