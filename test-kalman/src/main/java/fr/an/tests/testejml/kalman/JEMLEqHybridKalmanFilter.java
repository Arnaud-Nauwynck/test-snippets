package fr.an.tests.testejml.kalman;

import java.util.Arrays;

import org.ejml.data.DenseMatrix64F;
import org.ejml.equation.Equation;
import org.ejml.equation.Sequence;

/**
 * implementation of HybridKalmanFilter using JEML equation text compilations
 * 
 * http://www.idsc.ethz.ch/content/dam/ethz/special-interest/mavt/dynamic-systems-n-control/idsc-dam/Lectures/Recursive-Estimation/Lecture09.pdf
 *
 */
public class JEMLEqHybridKalmanFilter implements HybridKalmanFilter {

    // system state estimate
    private DenseMatrix64F x,x0,P;
    
    private DenseMatrix64F cstF0;
    private DenseMatrix64F F;
    private DenseMatrix64F G;
    private DenseMatrix64F Q;
    
    protected Equation eqt = new Equation();
    protected Sequence predictXt;
    protected Sequence predictXtWithoutU;
    protected Sequence predictXtWithU;
    protected Sequence predictPt;
    
    protected class MeasureSetEq {
        protected final String name;
        protected final int measureSetIndex;
        protected DenseMatrix64F H;
        protected DenseMatrix64F R;
        protected DenseMatrix64F z, y, K;
        protected Equation eqm = new Equation();
        protected Sequence updateY,updateK,updateX,updateP;
        
        public MeasureSetEq(int i, String name, int dimZ, int dimR) {
            this.name = name;
            this.measureSetIndex = i;
            z = new DenseMatrix64F(dimZ, 1);
            y = new DenseMatrix64F(dimZ, 1);
            K = new DenseMatrix64F(x.getNumRows(), dimZ);
            // R = new DenseMatrix64F(dimR, dimR);
        }

        public DenseMatrix64F getInnovVector() {
            return y; // eqm.lookup("y");
        }
        
        public DenseMatrix64F getKalmanGain() {
            return y; // eqm.lookup("y");
        }
        
        public DenseMatrix64F getH() {
            return H;
        }

        public DenseMatrix64F getR() {
            return R;
        }

        @Override
        public String toString() {
            return "MeasureSetEq [" + name + ", " + measureSetIndex + "]";
        }
        
    }

    protected MeasureSetEq[] measureSetEqs = new MeasureSetEq[0]; 
    
    
    // ------------------------------------------------------------------------

    public JEMLEqHybridKalmanFilter() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void initStateTransition(int dimX, int dimU, int dimW) {
        x = new DenseMatrix64F(dimX, 1);
        P = new DenseMatrix64F(dimX, dimX);
        x0 = new DenseMatrix64F(dimX, 1);
        eqt.alias(x,"x",x0,"x0",P,"P");
        eqt.alias(new DenseMatrix64F(dimU, 1), "u"); // zero if not overriden by setControlCommand()
        aliasDummy(eqt, "x0", "cstF0", "F", "G", "Q");
        eqt.alias(0.001, "dt");
        
        predictXtWithoutU = eqt.compile("x = x + (cstF0 + F*(x-x0))*dt");
        predictXtWithU = eqt.compile("x = x + (cstF0 + F*(x-x0))*dt + G*u*dt");
        predictXt = predictXtWithoutU; 
        predictPt = eqt.compile("P = P + F*P*dt + P*F'*dt + Q*Q'*dt");
    }

    @Override
    public int initNthMeasureTransition(String measureSetName, int dimZ, int dimV) {
        int i = measureSetEqs.length;
        this.measureSetEqs = Arrays.copyOf(measureSetEqs, i + 1);
        MeasureSetEq m = new MeasureSetEq(i, measureSetName, dimZ, dimV);
        this.measureSetEqs[i] = m;
        Equation eqm = m.eqm;
        eqm.alias(x, "x");
        eqm.alias(x0, "x0");
        eqm.alias(P, "P");
        
        eqm.alias(m.y, "y");
        eqm.alias(m.K, "K");
        eqm.alias(m.z, "z");
        eqm.alias(m.R, "R");
        aliasDummy(eqm, "H", "R");
        
        m.updateY = eqm.compile("y = z - H*x");  // may use ? "y = z - cstH0 - H*(x-x0)"
        // TODO use pinv() ? 
        m.updateK = eqm.compile("K = P*H' * pinv( H*P*H' + R*R')");
        m.updateX = eqm.compile("x = x + K*y");
        m.updateP = eqm.compile("P = P - K*H*P");

        return i;
    }

    @Override
    public void setStateTransition(DenseMatrix64F x0, DenseMatrix64F cstF0, DenseMatrix64F F, DenseMatrix64F G, DenseMatrix64F Q) {
        if (x0.getNumRows() != x.numRows) throw new IllegalArgumentException();
        if (x0.getNumCols() != x.numCols) throw new IllegalArgumentException();
        if (x.getNumRows() != F.numRows) throw new IllegalArgumentException();
        if (x.getNumRows() != F.numCols) throw new IllegalArgumentException();
        if (G != null) {
            // DenseMatrix64F u = eq.lookupMatrix("u");
            // if (u.getNumRows() != G.numCols) throw new IllegalArgumentException(); // TOADD
            if (x.getNumRows() != G.numRows) throw new IllegalArgumentException();
        }
        if (x.getNumRows() != Q.numRows) throw new IllegalArgumentException();
        if (x.getNumRows() != Q.numCols) throw new IllegalArgumentException();

        this.x0 = x0;
        this.cstF0 = cstF0;
        this.F = F;
        this.G = G;
        this.Q = Q;
        eqt.alias(x0,"x0", cstF0, "cstF0", F, "F", Q, "Q");
        if (G != null) {
            eqt.alias(G,"G");
            predictXt = predictXtWithU; 
        } else {
            predictXt = predictXtWithoutU; 
        }
    }
    
    public boolean isValid() {
        return F != null; 
    }
    
    @Override
    public void setNthMeasureTransition(int i, DenseMatrix64F H, DenseMatrix64F R) {
        MeasureSetEq m = this.measureSetEqs[i];
        m.H = H;
        m.R = R;
        m.eqm.alias(H, "H");
        m.eqm.alias(R, "R");
    }

    @Override
    public void setState(DenseMatrix64F x, DenseMatrix64F P) {
        this.x.set(x);
        this.P.set(P);
    }
    
    @Override
    public void setControlCommand(DenseMatrix64F u) {
        eqt.alias("u", u);
    }

    @Override
    public DenseMatrix64F getControlCommand() {
        return eqt.lookupMatrix("u");
    }

    @Override
    public void predictTimeStep(double dt) {
        eqt.alias(dt, "dt");
        predictXt.perform();
        predictPt.perform();
    }

    @Override
    public void updateNthMeasure(int measureIndex, DenseMatrix64F z) {
        MeasureSetEq m = this.measureSetEqs[measureIndex];
        m.eqm.alias(z, "z");

        m.updateY.perform();
        m.updateK.perform();
        m.updateX.perform();
        m.updateP.perform();
    }

    @Override
    public DenseMatrix64F getState() {
        return x;
    }

    @Override
    public DenseMatrix64F getState0() {
        return x0;
    }

    @Override
    public DenseMatrix64F getCovariance() {
        return P;
    }
    
    public DenseMatrix64F getCstF0() {
        return cstF0;
    }
    
    public DenseMatrix64F getF() {
        return F;
    }

    public DenseMatrix64F getG() {
        return G;
    }

    public DenseMatrix64F getQ() {
        return Q;
    }

    public DenseMatrix64F getNthMeasureH(int measureIndex) {
        return this.measureSetEqs[measureIndex].H;
    }

    public DenseMatrix64F getNthMeasureR(int measureIndex) {
        return this.measureSetEqs[measureIndex].R;
    }

    public DenseMatrix64F getNthMeasureInnov(int measureIndex) {
        return this.measureSetEqs[measureIndex].getInnovVector();
    }
    
    public DenseMatrix64F getNthMeasureKalmanGain(int measureIndex) {
        return this.measureSetEqs[measureIndex].getKalmanGain();
    }

    protected static void aliasDummy(Equation eq, String... names) {
        for(String name : names) {
            eq.alias(new DenseMatrix64F(1,1), name);
        }
    }
}
