package fr.an.tests.testejml.invertedpendulum;

import java.text.DecimalFormat;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
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
        modelParams.setFricCart(0.0); // TODO friction 
        // modelParams.setInertia(0.006);
        modelParams.setParamPoleLength(0.3);
        
        SimpleInvertedPendulumKalmanFilter sut = new SimpleInvertedPendulumKalmanFilter(modelParams, null);

        HybridKalmanFilter kf = sut.getKalmanFilter();
        
        DenseMatrix64F F = kf.getF();
        System.out.println("F=\n" + DenseMatrix64FUtils.matToString(F, null));

        final double epsDotPos = 1e-3;
        for(int i_dotPos = -1000; i_dotPos < 1000; i_dotPos++) {
            double dotPos0 = i_dotPos * epsDotPos; 
            
            DenseMatrix64F x0 = new DenseMatrix64F(4, 1);
            x0.set(0, dotPos0);
            
            doTestLinearizeAtX0(sut, x0);
        }

        
        final double epsDotAngle = 1e-3;
        for(int i_dotAngle = -1000; i_dotAngle < 1000; i_dotAngle++) {
            double dotAngle0 = i_dotAngle * epsDotAngle; 
            
            DenseMatrix64F x0 = new DenseMatrix64F(4, 1);
            x0.set(2, dotAngle0);
            
            doTestLinearizeAtX0(sut, x0);
        }
        
        
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
        final double dt = 1e-3;
        final double eps = 1e-5;
        final double prec = dt*eps*10.0; // = 1e-7  
        HybridKalmanFilter kf = sut.getKalmanFilter();

        DenseMatrix64F x1 = new DenseMatrix64F(4, 1); 
        DenseMatrix64F P = new DenseMatrix64F(4, 4); 

        DenseMatrix64F predictLinear = new DenseMatrix64F(4, 1); 
        DenseMatrix64F predictNonLinear = new DenseMatrix64F(4, 1); 
        
        kf.setState(x0, P);
        sut.updateLinearize();
        DenseMatrix64F F = kf.getF();
        DenseMatrix64F cstF0 = kf.getCstF0();
        
        DenseMatrix64F x = new DenseMatrix64F(4, 1); 
        DenseMatrix64F dx = new DenseMatrix64F(4, 1);
        DenseMatrix64F Fdx = new DenseMatrix64F(4, 1);
        DenseMatrix64F checkPredictLinear = new DenseMatrix64F(4,1);
        
        
        x1.set(x0);
        dx.set(0, 0.0); dx.set(1, 0.0); dx.set(2, 0.0); dx.set(3, 0.0);
        
        // compare nonLinear and linear result ... 
        sut.evalPredictTimeStepNonLinear(predictNonLinear, dt, x1);
        
        // check using linear x=x+F(x-x0)dt
        sut.setUseLinearPredict(true);
        x.set(x1);
        kf.setState(x, P);
        kf.predictTimeStep(dt);
        predictLinear.set(kf.getState());
        
        assertEquals(predictNonLinear, predictLinear, PREC);
        
        
        for (int i = 0; i < 4; i++) {
            x1.set(x0);
            x1.set(i, x0.get(i) + eps);
     
            dx.set(0, 0.0); dx.set(1, 0.0); dx.set(2, 0.0); dx.set(3, 0.0);
            dx.set(i, eps);
            
            // compare nonLinear and linear result ... 
//            sut.setUseLinearPredict(false);
//            x.set(x1);
//            kf.setState(x, P);
//            kf.predictTimeStep(dt);
//            .set(kf.getState());
            sut.evalPredictTimeStepNonLinear(predictNonLinear, dt, x1);
            
            // check using linear x=x+F(x-x0)dt
            sut.setUseLinearPredict(true);
            x.set(x1);
            kf.setState(x, P);
            kf.predictTimeStep(dt);
            predictLinear.set(kf.getState());
            
            CommonOps.mult(F, dx, Fdx);
            CommonOps.add(Fdx, cstF0, Fdx);
            CommonOps.scale(dt, Fdx, Fdx);
            CommonOps.add(Fdx, x1, checkPredictLinear);
            assertEquals(predictLinear, checkPredictLinear, prec);
            
            assertEqualsElt(predictLinear, predictNonLinear, 0, 0, prec);
            assertEqualsElt(predictLinear, predictNonLinear, 1, 0, prec);
            assertEqualsElt(predictLinear, predictNonLinear, 2, 0, prec);
            assertEqualsElt(predictLinear, predictNonLinear, 3, 0, prec);
        }
    }


    private void assertEquals(DenseMatrix64F expected, DenseMatrix64F actual, double prec) {
        for(int i = 0; i < actual.getNumRows(); i++) {
            for (int j = 0; j < actual.getNumCols(); j++) {
                assertEqualsElt(expected, actual, i, j, prec);
            }
        }
    }
    
    
    private void assertEqualsElt(DenseMatrix64F expected, DenseMatrix64F actual, int i, int j, double prec) {
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
