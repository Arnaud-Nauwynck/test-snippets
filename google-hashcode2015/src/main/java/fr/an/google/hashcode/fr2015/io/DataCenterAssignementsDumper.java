package fr.an.google.hashcode.fr2015.io;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import fr.an.google.hashcode.fr2015.model.DataCenter;
import fr.an.google.hashcode.fr2015.model.Group;
import fr.an.google.hashcode.fr2015.model.RemainingServerTypeCountPerServerType;
import fr.an.google.hashcode.fr2015.model.Row;
import fr.an.google.hashcode.fr2015.model.Row.RowRemainingSpacesCountPerSpaceType;
import fr.an.google.hashcode.fr2015.model.ServerType;

public class DataCenterAssignementsDumper {

    protected PrintStream out;
    protected boolean dumpRemaining;
    protected boolean dumpAssigned;
    
    
    public DataCenterAssignementsDumper(PrintStream out, boolean dumpRemaining, boolean dumpAssigned) {
        this.out = out;
        this.dumpRemaining = dumpRemaining;
        this.dumpAssigned = dumpAssigned;
    }


    
    public void dump(DataCenter src) {
        dumpRemainingServerTypeCounts(src);
        
        out.println();
        dumpRows(src, Arrays.asList(src.getRows()));

        out.println();
        dumpGroups(src, Arrays.asList(src.getGroups()));
    }



    public void dumpRemainingServerTypeCounts(DataCenter src) {
        out.println("ServerTypes : remaining DataCenter.countPerServerTypes");
        for(RemainingServerTypeCountPerServerType stc : src.getCountPerServerTypes().values()) {
            ServerType st = stc.serverType;
            out.println("ServerType:  cap=" + st.capacity  + ", size=" + st.size + " => count: " + stc.count);
        }
    }



    public void dumpRows(DataCenter src, Collection<Row> rows) {
        out.println("Rows:");
        for(Row r : rows) {
            dumpRow(r);
        }
    }



    public void dumpGroups(DataCenter src, Collection<Group> groups) {
        out.println("Groups:");
        for(Group g : groups) {
            dumpGroup(src, g);
        }
    }



    public void dumpGroup(DataCenter src, Group g) {
        out.println("Group[" + g.id + "]  totalCapacity:" + g.getTotalCapacity());
        Row[] rows = src.getRows();
        for(Row row : rows) {
            // row.
        }
    }



    public void dumpRow(Row r) {
        out.println("Row[" + r.id + "] unassigned remainingSize:" + r.getUnassignedTotalRemainingSize() + "");
        for(RowRemainingSpacesCountPerSpaceType spaceCount : r.getSpaceTypeCountPerSizeArray()) {
            if (spaceCount.getRemainingCount() == 0) continue;
            out.println("spaceCount[" + spaceCount.rowSpaceType.size + "] : remainingCount=" + spaceCount.getRemainingCount());
        }
    }
}
