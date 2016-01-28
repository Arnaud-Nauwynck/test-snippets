package fr.an.tests.testejml.invertedpendulum;

public class InvertedPendulumParams {

    // internal Model parameters
    double paramCartMass = 1.;
    public double paramPoleMass = 0.05;
    public double paramPoleLength = 1.;
    public double fricPole = 0.005;
    // friction... not modeled in observable model..
    public double fricCart = 0.00005;

    public double paramForceIncrMag = 5.;

    
    // ------------------------------------------------------------------------

    public InvertedPendulumParams() {
    }

    // ------------------------------------------------------------------------

    
    public double paramPoleLength() {
        return paramPoleLength;
    }

    public double getParamCartMass() {
        return paramCartMass;
    }

    public double getParamPoleMass() {
        return paramPoleMass;
    }

    public double getParamPoleLength() {
        return paramPoleLength;
    }

    public double getParamForceIncrMag() {
        return paramForceIncrMag;
    }

}
