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
    public void testInit() {
        // Prepare
        DataCenter dc = new DataCenterReader().readResource("dc.in");
        Row row0 = dc.getRow(0);
        if (DEBUG) dumper.dumpRow(row0);
//        Row[0] unassigned remainingSize:269
//        spaceCount[74] : remainingCount=1
//        spaceCount[52] : remainingCount=1
//        spaceCount[48] : remainingCount=1
//        spaceCount[43] : remainingCount=1
//        spaceCount[41] : remainingCount=1
//        spaceCount[11] : remainingCount=1
        int spaceSize0 = 74;
        int spaceSize1 = 52;
        int spaceSize2 = 48;
        int spaceSizeLast = 11;
        RowRemainingSpacesCountPerSpaceType head0 = row0.getSortedSpaceTypeCountListHead();
        RowRemainingSpacesCountPerSpaceType tail = row0.getSortedSpaceTypeCountListTail();
        
        RowRemainingSpacesCountPerSpaceType count0 = row0.getSpaceTypeCountPerSize(spaceSize0);
        RowRemainingSpacesCountPerSpaceType count1 = row0.getSpaceTypeCountPerSize(spaceSize1);
        RowRemainingSpacesCountPerSpaceType count2 = row0.getSpaceTypeCountPerSize(spaceSize2);
        RowRemainingSpacesCountPerSpaceType countLast = row0.getSpaceTypeCountPerSize(spaceSizeLast);


        Assert.assertSame(count0, head0.getNextRemaining());
        Assert.assertSame(head0, count0.getPrevRemaining());

        Assert.assertSame(count1, count0.getNextRemaining());
        Assert.assertSame(count0, count1.getPrevRemaining());

        Assert.assertSame(count2, count1.getNextRemaining());
        Assert.assertSame(count1, count2.getPrevRemaining());

        Assert.assertSame(tail, countLast.getNextRemaining());
        Assert.assertSame(countLast, tail.getPrevRemaining());
    }
    
    
    @Test
    public void testAssignIncrServerTypeToSpaceType() {
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
        int spaceSize1 = 52;
        int spaceSize2 = 48;
        int spaceSizeLast = 11;
        RowRemainingSpacesCountPerSpaceType head0 = row0.getSortedSpaceTypeCountListHead();
        RowRemainingSpacesCountPerSpaceType tail = row0.getSortedSpaceTypeCountListTail();
        
        RowRemainingSpacesCountPerSpaceType count0 = row0.getSpaceTypeCountPerSize(spaceSize0);
        RowRemainingSpacesCountPerSpaceType count1 = row0.getSpaceTypeCountPerSize(spaceSize1);
        RowRemainingSpacesCountPerSpaceType count2 = row0.getSpaceTypeCountPerSize(spaceSize2);
        RowRemainingSpacesCountPerSpaceType countLast = row0.getSpaceTypeCountPerSize(spaceSizeLast);
        
        int spaceSizeLeft01 = 72;
        RowRemainingSpacesCountPerSpaceType newCount01 = row0.getSpaceTypeCountPerSize(spaceSizeLeft01);
        Assert.assertEquals(0, newCount01.getRemainingCount());
        
        int server0Cap = 10;
        int server0Size = 2;
        ServerType st = dc.getOrCreateCountPerServerType(server0Cap, server0Size).serverType;
        int stCount = 1;
        // Perform
        // assign 1 x server (size=2)   => decr 1 occurrences of space 74, incr 1 occ of space 72, totalSlots=4
        row0.assignIncrServerTypeToSpaceType(spaceSize0, st, stCount);
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

        RowRemainingSpacesCountPerSpaceType countSize0 = row0.getSpaceTypeCountPerSize(spaceSize0);
        Assert.assertEquals(0,  countSize0.remainingCount);
        RowRemainingSpacesCountPerSpaceType countLeftSize0 = row0.getSpaceTypeCountPerSize(spaceSize0-server0Size);
        Assert.assertEquals(1,  countLeftSize0.remainingCount);

        final int row0RemainingSizeAfter = row0.getUnassignedTotalRemainingSize();
        Assert.assertEquals(row0RemainingSizeAfter, row0RemainingSizeBefore - server0Size*stCount);
        
        Assert.assertSame(newCount01, head0.getNextRemaining());
        Assert.assertSame(head0, newCount01.getPrevRemaining());
        
        Assert.assertEquals(0,  count0.remainingCount);  

        Assert.assertSame(count1, newCount01.getNextRemaining());
        Assert.assertSame(newCount01, count1.getPrevRemaining());
        
        Assert.assertSame(count2, count1.getNextRemaining());
        Assert.assertSame(count1, count2.getPrevRemaining());
        
        
        // Prepare
        
        // Perform
        row0.assignIncrServerTypeToSpaceType(spaceSizeLast, st, stCount);
        // Post-check
        int spaceSizeLeftLast = spaceSizeLast - st.size;
        RowRemainingSpacesCountPerSpaceType newCountLast = row0.getSpaceTypeCountPerSize(spaceSizeLeftLast);
        Assert.assertEquals(0,  countLast.remainingCount);  
        Assert.assertEquals(1,  newCountLast.remainingCount);  

        Assert.assertSame(tail, newCountLast.getNextRemaining());
        Assert.assertSame(newCountLast, tail.getPrevRemaining());
        
        Assert.assertEquals(41, newCountLast.getPrevRemaining().getSize());
    }
}
