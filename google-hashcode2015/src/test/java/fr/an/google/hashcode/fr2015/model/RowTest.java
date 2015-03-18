package fr.an.google.hashcode.fr2015.model;

import org.junit.Assert;
import org.junit.Test;

import fr.an.google.hashcode.fr2015.io.DataCenterAssignementsDumper;
import fr.an.google.hashcode.fr2015.io.DataCenterReader;
import fr.an.google.hashcode.fr2015.model.Row.RowRemainingSpacesCountPerSpaceType;


public class RowTest {
    DataCenterAssignementsDumper dumper = new DataCenterAssignementsDumper(System.out, true, true);
    private boolean DEBUG = false;
    
    @Test
    public void testAssignIncrServerTypeToSlotSeqType() {
        // Prepare
        DataCenter dc = new DataCenterReader().readResource("dc.in");
        Row row0 = dc.getRow(0);
        final int row0RemainingSizeBefore = row0.getUnassignedTotalRemainingSize();
        if (DEBUG) dumper.dumpRow(row0);
//        Row[0] unassigned remainingSize:269
//        spaceCount[74] : remainingCount=1
//        spaceCount[52] : remainingCount=1
//        spaceCount[48] : remainingCount=1
//        spaceCount[43] : remainingCount=1
//        spaceCount[41] : remainingCount=1
//        spaceCount[11] : remainingCount=1
        int spaceSize0 = 74;
        
        int server0Cap = 10;
        int server0Size = 2;
        ServerType st = dc.getOrCreateCountPerServerType(server0Cap, server0Size).serverType;
        int stCount = 1;
        // Perform
        // assign 1 x server (size=2)   => decr 1 occurrences of space 74, incr 1 occ of space 72, totalSlots=4
        row0.assignIncrServerTypeToSlotSeqType(spaceSize0, st, stCount);
        // Post-check
        if (DEBUG) {
            System.out.println("=> after assign:");
            dumper.dumpRow(row0);
        }
//        => after assign:
//            Row[0] unassigned remainingSize:267
//            spaceCount[72] : remainingCount=1
//            spaceCount[52] : remainingCount=1
//            spaceCount[48] : remainingCount=1
//            spaceCount[43] : remainingCount=1
//            spaceCount[41] : remainingCount=1
//            spaceCount[11] : remainingCount=1

        RowRemainingSpacesCountPerSpaceType countSize0 = row0.getSpaceTypeCountOrNull(spaceSize0);
        Assert.assertEquals(0,  countSize0.remainingCount);
        RowRemainingSpacesCountPerSpaceType countLeftSize0 = row0.getSpaceTypeCountOrNull(spaceSize0-server0Size);
        Assert.assertEquals(1,  countLeftSize0.remainingCount);

        final int row0RemainingSizeAfter = row0.getUnassignedTotalRemainingSize();
        Assert.assertEquals(row0RemainingSizeAfter, row0RemainingSizeBefore - server0Size*stCount);
    }
}
