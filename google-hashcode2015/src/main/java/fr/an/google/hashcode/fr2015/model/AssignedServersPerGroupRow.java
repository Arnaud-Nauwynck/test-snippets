package fr.an.google.hashcode.fr2015.model;

import java.util.HashMap;
import java.util.Map;

public final class AssignedServersPerGroupRow {
    
    public final Group group;
    public final Row row;
    
    private int assignedCount;
    private int assignedCapacity;

    private Map<ServerType,AssignedPerServerTypeGroupRow> assignCountPerServerType = new HashMap<ServerType,AssignedPerServerTypeGroupRow>(); 
    
    // ------------------------------------------------------------------------

    public AssignedServersPerGroupRow(Group group, Row row) {
        this.group = group;
        this.row = row;
    }

    // ------------------------------------------------------------------------

    public AssignedPerServerTypeGroupRow getOrCreateAssignCountPerServerTypes(ServerType st) {
        AssignedPerServerTypeGroupRow res = assignCountPerServerType.get(st);
        if (res == null) {
            res = new AssignedPerServerTypeGroupRow(st);
            assignCountPerServerType.put(st, res);
        }
        return res;
    }
    
    public void assignIncrServerCount(ServerType st, int count) {
        AssignedPerServerTypeGroupRow c = getOrCreateAssignCountPerServerTypes(st);
        c.assignIncr(count);
        
        assignedCount += count;
        assignedCapacity += count * st.capacity;
    }

    // ------------------------------------------------------------------------

    
    public Group getGroup() {
        return group;
    }

    public Row getRow() {
        return row;
    }

    public int getAssignedCount() {
        return assignedCount;
    }

    public int getAssignedCapacity() {
        return assignedCapacity;
    }
    
    
}