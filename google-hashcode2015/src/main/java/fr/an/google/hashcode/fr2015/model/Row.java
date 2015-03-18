package fr.an.google.hashcode.fr2015.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.an.google.hashcode.fr2015.model.occ.SlotOcc;
import fr.an.google.hashcode.fr2015.model.occ.SlotSequenceOcc;


public final class Row {

	public final int id;
	private final int rowSize; // useless in solving ... cf usableCount instead 

	
	int assignedTotalCapacity;
    int unassignedTotalRemainingSize;
	
	public static class RowRemainingSpacesCountPerSpaceType {
	    public final RowSpaceType rowSpaceType; // = [row,size]

	    List<SlotSequenceOcc> slotSeqOccs = new ArrayList<SlotSequenceOcc>();
	    
	    // count number of remaining Spaces (Slot sequence) of size [size]
	    // when incr/decr assign => will add count to newly available spaces, and will decr count to used spaces 
	    int remainingCount;

        public RowRemainingSpacesCountPerSpaceType(RowSpaceType rowSpaceType) {
            this.rowSpaceType = rowSpaceType;
        }

        public void initAddOcc(SlotSequenceOcc slotSeqOcc) {
            slotSeqOccs.add(slotSeqOcc);
            remainingCount++;
        }

        public void assignIncr(ServerType st, int stCount) {
            remainingCount -= stCount;
        }

        public int getRemainingCount() {
            return remainingCount;
        }
        
	}
	
	private final Map<Integer,RowRemainingSpacesCountPerSpaceType> spaceTypeCountPerSize = 
	        new TreeMap<Integer,RowRemainingSpacesCountPerSpaceType>(Collections.reverseOrder());
	
	// useless in solving, only to print solution (cf SlotSequenceType)
	private final SlotOcc[] slotOccs;
	// useless in solving, .. cf SlotSequenceTypeCount
	private final List<SlotSequenceOcc> slotSeqOccs = new ArrayList<SlotSequenceOcc>(); 
	
	// ------------------------------------------------------------------------

	public Row(int id, int rowSize, Set<Integer> unavailableSlotIds) {
		super();
		this.id = id;
		this.rowSize = rowSize;

		this.slotOccs = new SlotOcc[rowSize];
        for (int i = 0; i < rowSize; i++) {
            boolean usable = (unavailableSlotIds == null || ! unavailableSlotIds.contains(Integer.valueOf(i)));
            slotOccs[i] = new SlotOcc(this, i, usable);
	    }
        
        // determine Slot Sequence occurrences
        int seqStart = -1;
        for (int i = 0; i < rowSize; i++) {
            if (slotOccs[i].usable) {
                if (seqStart == -1) { // detected start of usable slots sequence
                    seqStart = i;
                }
            } else {
                if (seqStart != -1) { // detected end of usable slots sequence
                    int seqSize = i - seqStart + 1;
                    RowRemainingSpacesCountPerSpaceType countPerSpaceType = getOrCreateCountPerSpaceType(seqSize);
                    RowSpaceType rowSpaceType = countPerSpaceType.rowSpaceType;
                    
                    // register slot seq occ into SlotSequenceTypeCountPerRowAndSize
                    SlotSequenceOcc slotSeqOcc = new SlotSequenceOcc(rowSpaceType, seqStart);
                    slotSeqOccs.add(slotSeqOcc);
                    
                    countPerSpaceType.initAddOcc(slotSeqOcc); // => incr remainingCount
                    unassignedTotalRemainingSize += seqSize;
                }
            }
        }
	}

    // ------------------------------------------------------------------------

	
	private RowRemainingSpacesCountPerSpaceType getOrCreateCountPerSpaceType(int seqSize) {
	    RowRemainingSpacesCountPerSpaceType res = spaceTypeCountPerSize.get(seqSize);
	    if (res == null) {
	        RowSpaceType rowSpaceType = new RowSpaceType(this, seqSize);
	        res = new RowRemainingSpacesCountPerSpaceType(rowSpaceType);
	        spaceTypeCountPerSize.put(seqSize, res);
	    }
	    return res;
	}

    
    public void assignIncrServerTypeToSlotSeqType(int spaceSize, ServerType st, int stCount) {
        RowRemainingSpacesCountPerSpaceType count = getOrCreateCountPerSpaceType(spaceSize);
        count.assignIncr(st, stCount);

        int leftSpaceSize = spaceSize - st.size;
        assert leftSpaceSize >= 0;
        if (leftSpaceSize != 0) { 
            RowRemainingSpacesCountPerSpaceType leftSpaceCount = getOrCreateCountPerSpaceType(leftSpaceSize);
            leftSpaceCount.assignIncr(st, -stCount);
        }
        
        // update per row counts
        assignedTotalCapacity += st.capacity * stCount;
        unassignedTotalRemainingSize -= st.size * stCount;
    }

    public double getMaxScoreWithBestRemainingEfficiency(double eff) {
        return assignedTotalCapacity + unassignedTotalRemainingSize * eff;
    }
    
    
    
	public int getId() {
		return id;
	}
	
	public int getRowSize() {
		return rowSize;
	}
	
	public int getAssignedTotalCapacity() {
        return assignedTotalCapacity;
    }

    public int getUnassignedTotalRemainingSize() {
        return unassignedTotalRemainingSize;
    }

    public Map<Integer, RowRemainingSpacesCountPerSpaceType> getSpaceTypeCountPerSize() {
        return spaceTypeCountPerSize;
    }
    public RowRemainingSpacesCountPerSpaceType getSpaceTypeCountOrNull(int size) {
        return spaceTypeCountPerSize.get(size);
    }

    
    public List<SlotSequenceOcc> getSlotSeqOccs() {
        return slotSeqOccs;
    }

    public SlotOcc[] getSlotOccs() {
	    return slotOccs;
	}
	
    

    public String getSlotsAsString() {
		StringBuilder sb = new StringBuilder();
		for (SlotOcc slot : slotOccs) {
			if(!slot.usable) {
				sb.append('x');
//			} else if (!slot.isFree()) {
//				sb.append('o');
			} else {
				sb.append('_');
			}
		}
				
		return sb.toString();
	}

//    int getTotalCapacityByGroup(int groupId) {
//		int totalCapacity=0;
//		
//		for (SlotOcc slot : slotOccs) {
//			if( slot.usable && !slot.isFree() ) {
//				ServerOcc server = slot.getServer();
//				if(server!=null && server.getGroup().getId()==groupId) {
//					totalCapacity+=server.getCapacity();
//				}
//			}
//		}
//		
//		return totalCapacity;
//	}

	// ------------------------------------------------------------------------
    
	@Override
	public String toString() {
		return String.format("Row [id=%s, size=%s]", id, rowSize);
	}

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        Row other = (Row) obj;
        return id == other.id;
    }
	
	
}
