package fr.an.tests.testejml.kalman;

import java.util.Arrays;

import org.ejml.data.DenseMatrix64F;
import org.ejml.equation.Equation;
import org.ejml.equation.Sequence;

/**
 * implementation of HybridKalmanFilter using JEML equation text compilations
 *
 */
public class JEMLEqHybridKalmanFilter implements HybridKalmanFilter {

    // system state estimate
    private DenseMatrix64F x,P;
    
    
    private DenseMatrix64F F;
    private DenseMatrix64F G;
    private DenseMatrix64F Q;
    
    private Equation eq = new Equation();

    // Storage for precompiled code for predict and update
    Sequence predictXt;
    Sequence predictXtWithoutU;
    Sequence predictXtWithU;
    Sequence predictPt;
    
    private static class MeasureSetEq {
        final String name;
        final int measureSetIndex;
        DenseMatrix64F zi, Ri;
        Sequence updateYi,updateKi,updateXi,updatePi;
        final String aliasHi, aliasRi, aliasZi;
        
        public MeasureSetEq(int i, String name, int dimZ, int dimR) {
            this.name = name;
            this.measureSetIndex = i;
            zi = new DenseMatrix64F(dimZ, 1);
            Ri = new DenseMatrix64F(dimR, dimR);
            aliasHi = "H" + i;
            aliasRi = "R" + i;
            aliasZi = "z" + i;
        }

        @Override
        public String toString() {
            return "MeasureSetEq [" + name + ", " + measureSetIndex + "]";
        }
        
    }

    private MeasureSetEq[] measureSetEqs = new MeasureSetEq[0]; 
    
    
    // ------------------------------------------------------------------------

    public JEMLEqHybridKalmanFilter() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void initStateTransition(int dimX, int dimU, int dimW) {
        x = new DenseMatrix64F(dimX, 1);
        P = new DenseMatrix64F(dimX, dimX);
        eq.alias(x,"x",P,"P");
        eq.alias(new DenseMatrix64F(dimU, 1), "u"); // zero if not overriden by setControlCommand()
        aliasDummy("F", "G", "Q");
        eq.alias(0.001, "dt");
        
        predictXtWithoutU = eq.compile("x = x + F*x*dt");
        predictXtWithU = eq.compile("x = x + F*x*dt + G*u*dt");
        predictXt = predictXtWithoutU; 
        predictPt = eq.compile("P = P + F*P*F'*dt + Q*dt");
    }

    @Override
    public int initNthMeasureTransition(String measureSetName, int dimZ, int dimV) {
        int i = measureSetEqs.length;
        this.measureSetEqs = Arrays.copyOf(measureSetEqs, i + 1);
        MeasureSetEq m = new MeasureSetEq(i, measureSetName, dimZ, dimV);
        this.measureSetEqs[i] = m;
        eq.alias(m.zi, "z" + i);
        eq.alias(m.Ri, "R" + i);
        aliasDummy("H" + i, "R" + i);
        
        m.updateYi = eq.compile("y" + i + " = z" + i + " - H" + i + "*x");
        m.updateKi = eq.compile("K" + i + " = P*H" + i + "'*inv( H" + i + "*P*H" + i + "' + R" + i + " )");
        m.updateXi = eq.compile("x = x + K" + i + "*y" + i);
        m.updatePi = eq.compile("P = P-K" + i + "*(H" + i + "*P)");

        return i;
    }

    @Override
    public void setStateTransition(DenseMatrix64F F, DenseMatrix64F G, DenseMatrix64F Q) {
        if (x.getNumRows() != F.numRows) throw new IllegalArgumentException();
        if (x.getNumRows() != F.numCols) throw new IllegalArgumentException();
        if (G != null) {
            // DenseMatrix64F u = eq.lookupMatrix("u");
            // if (u.getNumRows() != G.numCols) throw new IllegalArgumentException(); // TOADD
            if (x.getNumRows() != G.numRows) throw new IllegalArgumentException();
        }
        if (x.getNumRows() != Q.numRows) throw new IllegalArgumentException();
        if (x.getNumRows() != Q.numCols) throw new IllegalArgumentException();

        this.F = F;
        this.G = G;
        this.Q = Q;
        eq.alias(F,"F",Q,"Q");
        if (G != null) {
            eq.alias(G,"G");
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
        eq.alias(m.aliasHi, H);
        eq.alias(m.aliasRi, R);
    }

    @Override
    public void setState(DenseMatrix64F x, DenseMatrix64F P) {
        this.x.set(x);
        this.P.set(P);
    }
    
    @Override
    public void setControlCommand(DenseMatrix64F u) {
        eq.alias("u", u);
    }

    @Override
    public void predictTimeStep(double dt) {
        eq.alias(dt, "dt");
        predictXt.perform();
        predictPt.perform();
    }

    @Override
    public void updateNthMeasure(int measureIndex, DenseMatrix64F z) {
        MeasureSetEq m = this.measureSetEqs[measureIndex];
        eq.alias(z, m.aliasZi);

        m.updateYi.perform();
        m.updateKi.perform();
        m.updateXi.perform();
        m.updatePi.perform();
    }

    @Override
    public DenseMatrix64F getState() {
        return x;
    }

    @Override
    public DenseMatrix64F getCovariance() {
        return P;
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

    protected void aliasDummy(String... names) {
        for(String name : names) {
            eq.alias(new DenseMatrix64F(1,1), name);
        }
    }
}
