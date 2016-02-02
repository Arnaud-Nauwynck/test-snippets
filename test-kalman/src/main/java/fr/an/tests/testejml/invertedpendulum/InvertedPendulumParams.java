package fr.an.tests.testejml.invertedpendulum;

public class InvertedPendulumParams {

    // internal Model parameters
    double paramCartMass = 1.;
    public double paramPoleMass = 0.1;
    public double paramPoleLength = 0.5;
    // public double paramInertia = 0.006;
    public double fricPole = 0.005;
    // friction... not modeled in observable model..
    public double fricCart = 0.0005;

    public double paramForceIncrMag = 2.;

//    x = [x_1, x_2, x_3, x_4] = [x_w, \dot{x}_w, \varphi, \dot{\varphi}]
//    l = 0.5     # length of the pendulum rod
//    g = 9.81    # gravitational acceleration
//    M = 1.0     # mass of the cart
//    m = 0.1     # mass of the pendulum
//
//    s = sin(x3)
//    c = cos(x3)
//
//    ff = np.array([                     x2,
//                   m*s*(-l*x4**2+g*c)/(M+m*s**2)+1/(M+m*s**2)*u1,
//                                        x4,
//            s*(-m*l*x4**2*c+g*(M+m))/(M*l+m*l*s**2)+c/(M*l+l*m*s**2)*u1
//                ])
    
            
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

    public double getFricPole() {
        return fricPole;
    }

    public void setFricPole(double fricPole) {
        this.fricPole = fricPole;
    }

    public double getFricCart() {
        return fricCart;
    }

    public void setFricCart(double fricCart) {
        this.fricCart = fricCart;
    }

    public void setParamCartMass(double paramCartMass) {
        this.paramCartMass = paramCartMass;
    }

    public void setParamPoleMass(double paramPoleMass) {
        this.paramPoleMass = paramPoleMass;
    }

    public void setParamPoleLength(double paramPoleLength) {
        this.paramPoleLength = paramPoleLength;
    }

    public void setParamForceIncrMag(double paramForceIncrMag) {
        this.paramForceIncrMag = paramForceIncrMag;
    }

    
    
}
