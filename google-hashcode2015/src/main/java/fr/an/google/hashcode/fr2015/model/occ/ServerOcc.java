package fr.an.google.hashcode.fr2015.model.occ;

import fr.an.google.hashcode.fr2015.model.Group;
import fr.an.google.hashcode.fr2015.model.ServerType;

public final class ServerOcc {
	
	private final int id;

	public final ServerType serverType;
	
	
	// from assignement
	private SlotOcc slot;
	private Group group;

	// ------------------------------------------------------------------------

	public ServerOcc(int id, ServerType serverType) {
	    super();
	    this.id = id;
	    this.serverType = serverType;
	}

	// ------------------------------------------------------------------------

	public int getId() {
		return id;
	}
	
	// ------------------------------------------------------------------------


    public SlotOcc getSlot() {
        return slot;
    }
    public void setSlot(SlotOcc slot) {
        this.slot = slot;
    }

    public Group getGroup() {
        return group;
    }
    public void setGroup(Group group) {
        this.group = group;
    }
	
    // ------------------------------------------------------------------------

    
	@Override
	public String toString() {
		return String.format("ServerOcc[id=%s, capacity=%s, size=%s]", id, serverType.capacity, serverType.size);
	}

}
