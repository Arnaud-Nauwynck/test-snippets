package fr.an.google.hashcode.fr2015.model;

import java.util.HashMap;
import java.util.Map;

public final class Group {

	public final int id;
	
	private int totalCapacity;
		
	private Map<Row,AssignedServersPerGroupRow> assignedServerCountsPerRow = new HashMap<Row,AssignedServersPerGroupRow>();
	
	// ------------------------------------------------------------------------

    public Group(int id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------

	public AssignedServersPerGroupRow getOrCreateServerCountsPerRow(Row row) {
	    AssignedServersPerGroupRow res = assignedServerCountsPerRow.get(row);
	    if (res == null) {
	        res = new AssignedServersPerGroupRow(this, row);
	        assignedServerCountsPerRow.put(row, res);
	    }
	    return res;
	}
	
	public void addAssign(Row row, ServerType serverType, int count) {
	    incrAssign(row, serverType, count);
	}
	
	public void removeAssign(Row row, ServerType serverType, int count) {
        incrAssign(row, serverType, -count);
	}

	public void incrAssign(Row row, ServerType serverType, int incrServerCount) {
        totalCapacity -= incrServerCount * serverType.getCapacity();
        AssignedServersPerGroupRow sc = getOrCreateServerCountsPerRow(row);
        sc.assignIncrServerCount(serverType, incrServerCount);
	}

	
	public int getId() {
		return id;
	}

//	public Set<ServerOcc> getServers() {
//		return servers;
//	}
//	public void setServers(Set<ServerOcc> servers) {
//		this.servers = servers;
//	}

	public int getTotalCapacity() {
		return totalCapacity;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        Group other = (Group) obj;
        return id == other.id;
    }

}
