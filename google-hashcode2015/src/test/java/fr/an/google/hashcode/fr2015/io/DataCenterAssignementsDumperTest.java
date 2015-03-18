package fr.an.google.hashcode.fr2015.io;

import org.junit.Test;

import fr.an.google.hashcode.fr2015.model.DataCenter;


public class DataCenterAssignementsDumperTest {

    @Test
    public void testDump() {
        // Prepare
        DataCenter dc = new DataCenterReader().readResource("dc.in");
        DataCenterAssignementsDumper sut = new DataCenterAssignementsDumper(System.out, true, true);
        // Perform
        sut.dump(dc);
        // Perform
        // Post-check
    }
}
