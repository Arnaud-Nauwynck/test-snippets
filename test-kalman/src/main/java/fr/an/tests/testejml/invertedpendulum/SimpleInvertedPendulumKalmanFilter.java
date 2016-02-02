package fr.an.tests.testejml.invertedpendulum;

import java.text.DecimalFormat;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import fr.an.tests.testejml.kalman.JEMLEqHybridKalmanFilter;
import static fr.an.tests.testejml.util.DenseMatrix64FUtils.*;

public class SimpleInvertedPendulumKalmanFilter extends AbstractInvertedPendulumKalmanModel {

    private static final double cstG = 9.8;

    protected static final int STATE_POSDOT = 0;
    protected static final int STATE_POS = 1;
    protected static final int STATE_ANGLEDOT = 2;
    protected static final int STATE_ANGLE = 3;
    
    private int indexMeasureXAngle;
        
    // state origin for linearisation
    protected DenseMatrix64F x0 = new DenseMatrix64F(4, 1);
    
    protected DenseMatrix64F cstF0 = new DenseMatrix64F(4, 1);
    protected DenseMatrix64F F = new DenseMatrix64F(4, 4);
    protected DenseMatrix64F G = new DenseMatrix64F(4, 1);
    protected DenseMatrix64F Q = new DenseMatrix64F(4, 4);

    protected DenseMatrix64F H = new DenseMatrix64F(2, 4);
    protected DenseMatrix64F R = new DenseMatrix64F(2, 2);

    protected static double[] defaultQ = new double[] {
        1.,       0.00001, 0.00001, 0.00001, //
        0.000001, 1.,      0.00001, 0.00001, //
        0.000001, 0.00001, 1.,      0.00001, //
        0.000001, 0.00001, 0.00001, 1., 
    };
    
    protected static double[] defaultR = new double[] {
        0.00001, 0.0, //
        0.0, 0.00001 //
    };
    
    protected boolean useLinearPredict = false;
    protected double relinearizeWhenAngleMax = 0.0001; // ~always re-linearize 
        // 3.1415/6.0;
    
    private boolean debug = false;
    protected DenseMatrix64F errState = new DenseMatrix64F(4, 1);
    private static DecimalFormat df_state = new DecimalFormat("+00.000;-#");
    private static DecimalFormat df_err = new DecimalFormat("0.#E0");
    private static DecimalFormat df_cov = new DecimalFormat("+0.000;-#");

    // ------------------------------------------------------------------------
    
    public SimpleInvertedPendulumKalmanFilter(InvertedPendulumParams modelParams, InvertedPendulumModelMeasureSimulator modelSimulator) {
        super(modelParams, modelSimulator);
                
        G.set(STATE_POSDOT, 1.0);
        G.set(STATE_POS, 0.0);
        G.set(STATE_ANGLEDOT, 1.0);
        G.set(STATE_ANGLE, 0.0);

        Q.setData(defaultQ);

        H.setData(new double[] {
            0.0, 1.0, 0.0, 0.0,  //
            0.0, 0.0, 0.0, 1.0,  //
        });

        R.setData(defaultR);
        
        initKalmanModel();
    }
    
    
    // ------------------------------------------------------------------------
    
    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public boolean isUseLinearPredict() {
        return useLinearPredict;
    }

    public void setUseLinearPredict(boolean p) {
        this.useLinearPredict = p;
    }

    @Override
    protected void initKalmanModel() {
        this.kalmanFilter = new JEMLEqHybridKalmanFilter() {
            @Override
            public void predictTimeStep(double dt) {
                eqt.alias(dt, "dt");
                
                if (useLinearPredict) {
                    predictXt.perform();
                } else {
                    DenseMatrix64F x = getState();
                    evalPredictTimeStepNonLinear(x, dt, x);
                }

                // (optionnaly?) re-linearize each time...
                double deltaAngle = getState().get(3,0) - getState0().get(3,0);
                if (Math.abs(deltaAngle) > relinearizeWhenAngleMax) {
                    updateLinearize();
                }

                predictPt.perform();
                
                if (debug) {
                    debugDump("predictTimeStep " + dt, false, -1);
                }
                
            }

            @Override
            public void updateNthMeasure(int measureIndex, DenseMatrix64F z) {
                super.updateNthMeasure(measureIndex, z);
                
                if (debug) {
                    debugDump("updateNthMeasure[" + measureIndex + "] " + vectToString(z, df_state), true, measureIndex);
                }
            }
            protected void debugDump(String msg, boolean dumpKalman, int measureIndex) {
                DenseMatrix64F x = getState();
                DenseMatrix64F P = getCovariance();
                
                if (modelMeasureSimulator != null) {
                    InvertedPendulumModel realModel = modelMeasureSimulator.getModel();
                    DenseMatrix64F realState = realModel.getState();
                    CommonOps.subtract(realState, x, errState);
                    System.out.print(msg +  " =>\n"
                            + "estim:"+ vectToString(x, df_state) + "\n" 
                            + "real :" + vectToString(realState, df_state) + "\n"
                            + "err  : " + vectToString(errState, df_err) + "\n");
                }
                if (dumpKalman) {
                    MeasureSetEq m = measureSetEqs[measureIndex];
                    DenseMatrix64F inov = m.getInnovVector();
                    DenseMatrix64F kalmanGain = m.getKalmanGain();
                    System.out.print(
                          "inov :" + vectToString(inov, df_err) + "\n"
                         + "gain :" + vectToString(kalmanGain, df_state) + "\n");
                }
                System.out.print("cov:\n" + matToString(P, df_cov) + "\n");
            }
        };
        this.kalmanFilter.initStateTransition(4, 1, 2);
        indexMeasureXAngle = kalmanFilter.initNthMeasureTransition("XAngle", 2, 2);
                
        kalmanFilter.setNthMeasureTransition(indexMeasureXAngle, H, R);
        
        updateLinearize();
    }
    
    
    
    /**
     * TODO ... http://www.profjrwhite.com/system_dynamics/sdyn/s7/s7invp2/s7invp2.html
     */
    protected void updateLinearize() {
        DenseMatrix64F x = kalmanFilter.getState();
        x0.set(x);
        
        // double pos = x.get(STATE_POS);
        double angle = x.get(STATE_ANGLE);
        double angleDot = x.get(STATE_ANGLEDOT);
        double posDot = x.get(STATE_POSDOT);
        double f = kalmanFilter.getControlCommand().get(0);
        
        
        double s_a = Math.sin(angle);
        double c_a = Math.cos(angle);
        double cm  = params.getParamCartMass();
        double pm = params.getParamPoleMass();
        double tm = cm + pm;
        double invTm = 1.0 / tm;
        double pl05 = 0.5 * params.getParamPoleLength();
        double pml = pl05 * pm;
        double fc = params.fricCart * (posDot < 0 ? -1 : 0);
        double fp = params.fricPole;
        
        // x=a+dx
        // cos(a+dx) = cos(a).cos(dx) - sin(a).sin(dx) ~= c_a - s_a * dx  
        // sin(a+dx) = sin(a).cos(dx) + cos(a).sin(dx) ~= s_a + c_a * dx 
                
        double common = (f + pml * angleDot * angleDot * s_a - fc) * invTm;
        double angleDDot = (cstG * s_a - c_a * common - fp * angleDot / pml)
            / (pl05 * (4. / 3. - pm * c_a * c_a * invTm));
        double posDDot = common - pml * angleDDot * c_a * invTm;
//
//        // Now update state.
//        pos += posDot * dt;
//        posDot += posDDot * dt;
//        angle += angleDot * dt;
//        angleDot += angleDDot * dt;

        double cstF0_0 = posDDot;
        double cstF0_1 = posDot; // x0.get(0);
        double cstF0_2 = angleDDot;
        double cstF0_3 = angleDot; // x0.get(2);
        
        // f0j= d(posDot)/dj
        //     = d(common - pml * angleDDot * c_a * invTm) / dj
        //     = invTm * d( f + pml * angleDot * angleDot * s_a - fc  - pml * angleDDot * c_a ) / dj
        double f00 = 0.0; // d(posDDot)/dposDot
        double f01 = 0.0;// d(posDDot)/dpos;
        double f02 = // d(posDDot)/dangleDot
                invTm * pml * 2.0 * angleDot * s_a;
        double f03 = // d(posDDot)/dangle
                invTm * ( pml * angleDot * angleDot * c_a - pml * angleDDot * (-s_a) ); 

        // f1j = d(pos)/dj
        double f10 = 1.0; // d(pos)/dposDot
        double f11 = 0.0; // d(pos)/dpos
        double f12 = 0.0; // d(pos)/dangleDot
        double f13 = 0.0; // d(pos)/dangle

        // f2j = d(angleDDot)/dj
        //     = d( (cstG * s_a - c_a * common - fp * angleDot / pml)
        //            / (pl05 * (4. / 3. - pm * c_a * c_a * invTm)) )/dj
        //     = d( (cstG * s_a - c_a * (f + pml * angleDot * angleDot * s_a - fc) * invTm - fp * angleDot / pml)
        //            / (pl05 * (4. / 3. - pm * c_a * c_a * invTm)) )/dj
        double angleDot_denom = 1.0 / (pl05 * (4. / 3. - pm * c_a * c_a * invTm)); 
        double f20 = 0.0; // d(angleDot)/dposDot
        double f21 = 0.0; // d(angleDot)/dpos
        double f22 = // d(angleDot)/dangleDot
                (- c_a * (pml * 2.0 * angleDot ) * invTm - fp  / pml) * angleDot_denom;
        double f23 = // d(angleDot)/dangle
                (   cstG * c_a 
                        + s_a * (f + pml * angleDot * angleDot * s_a - fc) * invTm 
                        - c_a * (pml * angleDot * angleDot * c_a) * invTm
                        ) * angleDot_denom
                - (
                        cstG * s_a 
                        - c_a * (f + pml * angleDot * angleDot * s_a - fc) * invTm - fp * angleDot / pml
                        )
                * angleDot_denom * angleDot_denom * (pl05 * (- pm * (-2.0) * s_a * c_a * invTm));

        // f3j = d(angle)/dj
        double f30 = 0.0; // d(angle)/dposDot
        double f31 = 0.0; // d(angle)/dpos
        double f32 = 1.0; // d(angle)/dangleDot
        double f33 = 0.0; // d(angle)/dangle

        cstF0.set(0, 0, cstF0_0); cstF0.set(1, 0, cstF0_1); cstF0.set(2, 0, cstF0_2); cstF0.set(3, 0, cstF0_3);
        
        F.set(0, 0, f00); F.set(0, 1, f01); F.set(0, 2, f02); F.set(0, 3, f03);
        F.set(1, 0, f10); F.set(1, 1, f11); F.set(1, 2, f12); F.set(1, 3, f13);
        F.set(2, 0, f20); F.set(2, 1, f21); F.set(2, 2, f22); F.set(2, 3, f23);
        F.set(3, 0, f30); F.set(3, 1, f31); F.set(3, 2, f32); F.set(3, 3, f33);
        
        G.set(0, f * invTm);
        G.set(1, 0.0);
        G.set(2, - f * c_a * invTm);
        G.set(3, 0.0);

        kalmanFilter.setStateTransition(x0, cstF0, F, G, Q);
    }

    public void evalPredictTimeStepNonLinear(DenseMatrix64F res, double dt, DenseMatrix64F x) {
        double posDot = x.get(STATE_POSDOT);
        double pos = x.get(STATE_POS);
        double angleDot = x.get(STATE_ANGLEDOT);
        double angle = x.get(STATE_ANGLE);
        double f = kalmanFilter.getControlCommand().get(0);
        
        double s_a = Math.sin(angle);
        double c_a = Math.cos(angle);
        double cm  = params.getParamCartMass();
        double pm = params.getParamPoleMass();
        double tm = cm + pm;
        double invTm = 1.0 / tm;
        double pl05 = 0.5 * params.getParamPoleLength();
        double pml = pl05 * pm;
        double fc = 0.0; // TODO friction..   params.fricCart * (posDot < 0 ? -1 : 0);
        double fp = params.fricPole;
        
        double common = (f + pml * angleDot * angleDot * s_a - fc) * invTm;
        double angleDDot = (cstG * s_a - c_a * common - fp * angleDot / pml)
            / (pl05 * (4. / 3. - pm * c_a * c_a * invTm));
        double posDDot = common - pml * angleDDot * c_a * invTm;

        // Now update state.
        pos += posDot * dt;
        posDot += posDDot * dt;
        angle += angleDot * dt;
        angleDot += angleDDot * dt;
        
        res.set(STATE_POSDOT, posDot);
        res.set(STATE_POS, pos);
        res.set(STATE_ANGLEDOT, angleDot);
        res.set(STATE_ANGLE, angle);
    }

    
    @Override
    protected void simulateNoisedSensorMeasures() {
        DenseMatrix64F simulatorZ_XAngle = modelMeasureSimulator.getSimulatorZ_XAngle();
        kalmanFilter.updateNthMeasure(indexMeasureXAngle, simulatorZ_XAngle);
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
