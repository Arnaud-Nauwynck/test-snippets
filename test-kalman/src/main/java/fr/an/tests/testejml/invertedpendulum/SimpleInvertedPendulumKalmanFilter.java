package fr.an.tests.testejml.invertedpendulum;

import static fr.an.tests.testejml.util.DenseMatrix64FUtils.matToString;
import static fr.an.tests.testejml.util.DenseMatrix64FUtils.vectToString;

import java.text.DecimalFormat;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import fr.an.tests.testejml.kalman.JEMLEqHybridKalmanFilter;

public class SimpleInvertedPendulumKalmanFilter extends AbstractInvertedPendulumKalmanModel {

    private static final double cstG = 9.8;

    protected static final int STATE_POSDOT = 0;
    protected static final int STATE_POS = 1;
    protected static final int STATE_ANGLEDOT = 2;
    protected static final int STATE_ANGLE = 3;
    
        
    // state origin for linearisation
    protected DenseMatrix64F x0 = new DenseMatrix64F(4, 1);
    
    protected DenseMatrix64F cstF0 = new DenseMatrix64F(4, 1);
    protected DenseMatrix64F matF = new DenseMatrix64F(4, 4);
    protected DenseMatrix64F matG = new DenseMatrix64F(4, 1);
    protected DenseMatrix64F matQ = new DenseMatrix64F(4, 4);

    protected boolean useLinearPredict = false;
    protected double relinearizeWhenAngleMax = 3.1415/10.0;
            //  0.01; // ~always re-linearize 

    
    private int indexMeasureXAngle;
    protected DenseMatrix64F matH_XAngle = new DenseMatrix64F(2, 4);
    protected DenseMatrix64F matR_XAngle = new DenseMatrix64F(2, 2);

    protected static double[] defaultQ = new double[] {
        0.1,    0.0001, 0.0001, 0.0001, //
        0.0001, 0.1,    0.0001, 0.0001, //
        0.0001, 0.0001, 0.1,    0.0001, //
        0.0001, 0.0001, 0.0001, 0.1, 
    };
    
    protected static double[] defaultR_XAngle = new double[] {
        0.001,   0.00001, //
        0.00001, 0.001 //
    };

    // used to interpolate speed from difference between current observed pos and prev observed pos (possibly with amort factor)
    // should be useless in real Kalman filter (not re-linearized..) ??
    private int prevObjCount = 3;
    protected long[] prevObsTimeMillis = new long[prevObjCount];
    protected double[] prevObsPos = new double[prevObjCount];
    protected double[] prevObsAngle = new double[prevObjCount];

    protected boolean useInterpolatedSpeedFromPrevObs = true;
    protected DenseMatrix64F z_DX_X_DAngle_Angle = new DenseMatrix64F(4, 1); 
    
    // using previous observation to interpolate speend by difference between prev observed pos - observed pos
    protected int indexMeasure_DX_X_DAngle_Angle;
    protected DenseMatrix64F matH_DX_X_DAngle_Angle = new DenseMatrix64F(4, 4);
    protected DenseMatrix64F matR_DX_X_DAngle_Angle = new DenseMatrix64F(4, 4);
    
    protected static double[] defaultR_DX_X_DAngle_Angle = new double[] {
        2*0.001, 0.00001, 0.00001, 0.00001, //
        0.00001, 0.001,   0.00001, 0.00001, //
        0.00001, 0.00001, 2*0.001, 0.00001, //
        0.00001, 0.00001, 0.00001, 0.001,   //
    };
    

    
    private boolean debug = false;
    protected DenseMatrix64F errState = new DenseMatrix64F(4, 1);
    private static DecimalFormat df_state = new DecimalFormat("+00.000;-#");
    private static DecimalFormat df_err = new DecimalFormat("0.0E0");
    private static DecimalFormat df_cov = new DecimalFormat("+0.00E0;-#"); // "+0.000000;-#";

    // ------------------------------------------------------------------------
    
    public SimpleInvertedPendulumKalmanFilter(InvertedPendulumParams modelParams, InvertedPendulumModelMeasureSimulator modelSimulator) {
        super(modelParams, modelSimulator);
                
        matG.set(STATE_POSDOT, 1.0);
        matG.set(STATE_POS, 0.0);
        matG.set(STATE_ANGLEDOT, 1.0);
        matG.set(STATE_ANGLE, 0.0);

        matQ.setData(defaultQ);

        matH_XAngle.setData(new double[] {
            0.0, 1.0, 0.0, 0.0,  //
            0.0, 0.0, 0.0, 1.0,  //
        });
        matR_XAngle.setData(defaultR_XAngle);

        matH_DX_X_DAngle_Angle.setData(new double[] {
            1.0, 0.0, 0.0, 0.0, //
            0.0, 1.0, 0.0, 0.0, //
            0.0, 0.0, 1.0, 0.0, //
            0.0, 0.0, 0.0, 1.0, //
        });
        matR_DX_X_DAngle_Angle.setData(defaultR_DX_X_DAngle_Angle);

        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < prevObjCount; i++) {
            prevObsTimeMillis[i] = startTime;
        }
        
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
                    debugDumpKalmanEstim("predictTimeStep " + dt, false);
                }
                
            }

            @Override
            public void updateNthMeasure(int measureIndex, DenseMatrix64F z) {
                int interpMeasureIndex = measureIndex;
                DenseMatrix64F interpZ = z;
                
                if (measureIndex == indexMeasureXAngle) {
                    // shift update prev obs ... 
                    long timeMillis = System.currentTimeMillis();
                    for (int i = 0; i < prevObjCount-1; i++) {
                        prevObsTimeMillis[i+1] = prevObsTimeMillis[i];
                        prevObsPos[i+1] = prevObsPos[i]; 
                        prevObsAngle[i+1] = prevObsAngle[i];
                    }
                    prevObsTimeMillis[0] = timeMillis;
                    prevObsPos[0] = z.get(0, 0); 
                    prevObsAngle[0] = z.get(1, 0);

                    // interpolate observed speed from [0]-[1] 
                    double dt01 = 0.001 * (prevObsTimeMillis[0] - prevObsTimeMillis[1]);
                    double invDt01 = 1.0 / Math.max(dt01, 0.008); // avoid div 0 (jvm time precision=~8ms)
                    double interpDotPos01 = invDt01 * (prevObsPos[0] - prevObsPos[1]);
                    double interpDotAngle01 = invDt01 * (prevObsAngle[0] - prevObsAngle[1]);
                    
                    // same with observed speed from [1]-[2]
                    double dt12 = 0.001 * (prevObsTimeMillis[1] - prevObsTimeMillis[2]);
                    double invDt12 = 1.0 / Math.max(dt12, 0.008);
                    double interpDotPos12 = invDt12 * (prevObsPos[1] - prevObsPos[2]);
                    double interpDotAngle12 = invDt12 * (prevObsAngle[1] - prevObsAngle[2]);
                    
                    // interpolate using time decreasing average: (3.0*d01 + 1.0*d12) / 4.0
                    double interpDotPos = 0.25 * (3.0 * interpDotPos01 + interpDotPos12);
                    double interpDotAngle = 0.25 * (3.0 * interpDotAngle01 + interpDotAngle12);
                    
                    
                    if (useInterpolatedSpeedFromPrevObs) {
                        // augment observed vector z [X, Angle] with interpolated speed => [DX, X, DAngle, Angle]
                        z_DX_X_DAngle_Angle.set(0, 0, interpDotPos);
                        z_DX_X_DAngle_Angle.set(1, 0, z.get(0, 0));
                        z_DX_X_DAngle_Angle.set(2, 0, interpDotAngle);
                        z_DX_X_DAngle_Angle.set(3, 0, z.get(1, 0));
                        
                        interpMeasureIndex = indexMeasure_DX_X_DAngle_Angle;
                        interpZ = z_DX_X_DAngle_Angle;
                        
                        if (debug) {
                            System.out.println("use speed interpol updateNthMeasure[" + measureIndex + "] " 
                                    + vectToString(z, df_state)
                                    + "-> [" + interpMeasureIndex + "] " + vectToString(z_DX_X_DAngle_Angle, df_state));
                        }
                    }
                }

                if (debug) {
                    debugDumpKalmanEstim("pre updateNthMeasure[" + interpMeasureIndex + "]", false);
                    debugDumpKalmanGain("update updateNthMeasure[" + interpMeasureIndex + "]", interpMeasureIndex, interpZ);
                }
                
                super.updateNthMeasure(interpMeasureIndex, interpZ);
                
                if (debug) {
                    debugDumpKalmanEstim("post updateNthMeasure[" + interpMeasureIndex + "]", true);
                }
            }
        };
        this.kalmanFilter.initStateTransition(4, 1, 2);

        indexMeasureXAngle = kalmanFilter.initNthMeasureTransition("XAngle", 2, 2);
        kalmanFilter.setNthMeasureTransition(indexMeasureXAngle, matH_XAngle, matR_XAngle);
        
        indexMeasure_DX_X_DAngle_Angle = kalmanFilter.initNthMeasureTransition("DX_X_DAngle_Angle", 4, 4);
        kalmanFilter.setNthMeasureTransition(indexMeasure_DX_X_DAngle_Angle, matH_DX_X_DAngle_Angle, matR_DX_X_DAngle_Angle);
        
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
        
        matF.set(0, 0, f00); matF.set(0, 1, f01); matF.set(0, 2, f02); matF.set(0, 3, f03);
        matF.set(1, 0, f10); matF.set(1, 1, f11); matF.set(1, 2, f12); matF.set(1, 3, f13);
        matF.set(2, 0, f20); matF.set(2, 1, f21); matF.set(2, 2, f22); matF.set(2, 3, f23);
        matF.set(3, 0, f30); matF.set(3, 1, f31); matF.set(3, 2, f32); matF.set(3, 3, f33);
        
        matG.set(0, f * invTm);
        matG.set(1, 0.0);
        matG.set(2, - f * c_a * invTm);
        matG.set(3, 0.0);

        kalmanFilter.setStateTransition(x0, cstF0, matF, matG, matQ);
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

    protected void debugDumpKalmanGain(String msg, int measureIndex, DenseMatrix64F z) {
        DenseMatrix64F inov = kalmanFilter.getNthMeasureInnov(measureIndex);
        DenseMatrix64F kalmanGain = kalmanFilter.getNthMeasureKalmanGain(measureIndex);
        System.out.print(
             "z   :" + vectToString(z, df_state) + "\n"
             + "inov :" + vectToString(inov, df_err) + "\n"
             + "gain :" + vectToString(kalmanGain, df_state) + "\n");
    }
    
    protected void debugDumpKalmanEstim(String msg, boolean dumpCov) {
        DenseMatrix64F x = kalmanFilter.getState();
        DenseMatrix64F P = kalmanFilter.getCovariance();
        
        System.out.print(msg +  " =>\n"
                + "estim:"+ vectToString(x, df_state) + "\n");
        if (modelMeasureSimulator != null) {
            InvertedPendulumModel realModel = modelMeasureSimulator.getModel();
            DenseMatrix64F realState = realModel.getState();
            CommonOps.subtract(realState, x, errState);
            System.out.print("real :" + vectToString(realState, df_state) + "\n"
                    + "err  : " + vectToString(errState, df_err) + "\n");
        }
        if (dumpCov) {
            System.out.print("cov:\n" + matToString(P, df_cov) + "\n");
        }
    }
    

}
