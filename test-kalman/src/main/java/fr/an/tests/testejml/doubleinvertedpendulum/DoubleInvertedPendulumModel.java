package fr.an.tests.testejml.doubleinvertedpendulum;

/**
 * cf 
 * http://pytrajectory.readthedocs.org/en/latest/guide/examples/con_double_pendulum.html 
 * http://far.in.tum.de/twiki/pub/Main/ChristianWachinger/pendulum.pdf
 * http://www.nt.ntnu.no/users/skoge/prost/proceedings/acc04/Papers/0715_FrA03.3.pdf
 * 
 */
public class DoubleInvertedPendulumModel {

    /*
    state_vars = sp.symbols('x, dx, phi1, dphi1, phi2, dphi2')
            input_vars = sp.symbols('F,')
            x, dx, phi1, dphi1, phi2, dphi2 = state_vars
            F, = input_vars

            # parameters
            l1 = 0.25                   # 1/2 * length of the pendulum 1
            l2 = 0.25                   # 1/2 * length of the pendulum 
            m1 = 0.1                    # mass of the pendulum 1
            m2 = 0.1                    # mass of the pendulum 2
            m = 1.0                     # mass of the car
            g = 9.81                    # gravitational acceleration
            I1 = 4.0/3.0 * m1 * l1**2   # inertia 1
            I2 = 4.0/3.0 * m2 * l2**2   # inertia 2

            param_values = {'l1':l1, 'l2':l2, 'm1':m1, 'm2':m2, 'm':m, 'g':g, 'I1':I1, 'I2':I2}

            # mass matrix
            M = Matrix([[      m+m1+m2,          (m1+2*m2)*l1*cos(phi1),   m2*l2*cos(phi2)],
                        [(m1+2*m2)*l1*cos(phi1),   I1+(m1+4*m2)*l1**2,   2*m2*l1*l2*cos(phi2-phi1)],
                        [  m2*l2*cos(phi2),     2*m2*l1*l2*cos(phi2-phi1),     I2+m2*l2**2]])

            # and right hand site
            B = Matrix([[ F + (m1+2*m2)*l1*sin(phi1)*dphi1**2 + m2*l2*sin(phi2)*dphi2**2 ],
                        [ (m1+2*m2)*g*l1*sin(phi1) + 2*m2*l1*l2*sin(phi2-phi1)*dphi2**2 ],
                        [ m2*g*l2*sin(phi2) + 2*m2*l1*l2*sin(phi1-phi2)*dphi1**2 ]])

            f = solve_motion_equations(M, B, state_vars, input_vars)

            # then we specify all boundary conditions
            a = 0.0
            xa = [0.0, 0.0, pi, 0.0, pi, 0.0]

            b = 4.0
            xb = [0.0, 0.0, 0.0, 0.0, 0.0, 0.0]

            ua = [0.0]
            ub = [0.0]

            # here we specify the constraints for the velocity of the car
            con = {0 : [-1.0, 1.0],
                   1 : [-2.0, 2.0]}

            # now we create our Trajectory object and alter some method parameters via the keyword arguments
            S = ControlSystem(f, a, b, xa, xb, ua, ub, constraints=con,
                              eps=2e-1, su=20, kx=2, use_chains=False,
                              use_std_approach=False)

            # time to run the iteration
            x, u = S.solve()
            */
}
