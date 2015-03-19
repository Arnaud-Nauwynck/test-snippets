package fr.an.google.hashcode.fr2015.solver;

import org.junit.Test;

import fr.an.google.hashcode.fr2015.io.DataCenterReader;
import fr.an.google.hashcode.fr2015.model.DataCenter;


public class RecursiveSolverTest {

    DataCenter dc = new DataCenterReader().readResource("dc.in");
    RecursiveSolver sut = new RecursiveSolver(dc); 
    
    @Test
    public void testSolve() {
        // Prepare
        long startTime = System.currentTimeMillis();
        // Perform
        sut.solve();
        // Post-check
        long timeSec = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("Done ... took " + timeSec + " s");
        
    }
}
