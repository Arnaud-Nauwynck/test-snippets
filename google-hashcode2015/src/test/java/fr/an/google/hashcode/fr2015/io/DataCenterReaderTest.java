package fr.an.google.hashcode.fr2015.io;

import org.junit.Assert;
import org.junit.Test;

import fr.an.google.hashcode.fr2015.io.DataCenterReader;
import fr.an.google.hashcode.fr2015.model.DataCenter;


public class DataCenterReaderTest {

    @Test
    public void testReadResource() {
        // Prepare
        DataCenterReader sut = new DataCenterReader();
        // Perform
        DataCenter res = sut.readResource("dc.in");
        // Post-check
        Assert.assertNotNull(res);
        // 16 100 80 45 625
        Assert.assertEquals(16, res.getRowsCount());
        Assert.assertEquals(100, res.getRowSize());
        // int unavailableSlotsCount = scn.nextInt();
        Assert.assertEquals(45, res.getGroupsCount());
        Assert.assertEquals(625, res.getServersCount());
    }
    
}
