package fr.an.google.hashcode.fr2015.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.an.google.hashcode.fr2015.model.occ.SlotOcc;
import fr.an.google.hashcode.fr2015.model.occ.SlotSequenceOcc;


public final class Row {

	public final int id;
	private final int rowSize; // useless in solving ... cf usableCount instead 

	
	int assignedTotalCapacity;
    int unassignedTotalRemainingSize;
	
	public static final class RowRemainingSpacesCountPerSpaceType {
	    public final RowSpaceType rowSpaceType; // = [row,size]
	    
	    List<SlotSequenceOcc> slotSeqOccs = new ArrayList<SlotSequenceOcc>();
	    
	    // count number of remaining Spaces (Slot sequence) of size [size]
	    // when incr/decr assign => will add count to newly available spaces, and will decr count to used spaces 
	    int remainingCount;

        // OPTIM ... double linked-list pointer for sorted list per decreasing remainingCountSize ... with skip 0 !!!
	    RowRemainingSpacesCountPerSpaceType nextRemaining;
        RowRemainingSpacesCountPerSpaceType prevRemaining;

        
        public Row getRow() {
            return rowSpaceType.row;
        }
        public int getSize() {
            return rowSpaceType.size;
        }

        @Override
        public String toString() {
            return "SpaceCount[" + ((rowSpaceType!=null)? rowSpaceType.size : -1) + ", Count=" + remainingCount + "]";
        }

        public RowRemainingSpacesCountPerSpaceType(RowSpaceType rowSpaceType) {
            this.rowSpaceType = rowSpaceType;
        }

        public void initAddOcc(SlotSequenceOcc slotSeqOcc) {
            slotSeqOccs.add(slotSeqOcc);
            remainingCount++;
        }

        public void assignIncr(ServerType st, int stCount) {
            boolean toAddInList = (remainingCount == 0);
            
            remainingCount -= stCount;
            
            // auto add/remove itself in sorted linked-list ... if count==0 before or count==0 after
            if (remainingCount == 0) {
                // remove self from sorted list
                nextRemaining.prevRemaining = prevRemaining;
                prevRemaining.nextRemaining = nextRemaining;
            } else if (toAddInList) {
                // add self to sorted list
                // step 1: need to find where to insert... (TODO VERY slow !!)
                Row row = rowSpaceType.row;
                final RowRemainingSpacesCountPerSpaceType[] array = row.spaceTypeCountPerSizeArray;
                RowRemainingSpacesCountPerSpaceType next = row.getSortedSpaceTypeCountListTail(); // != array[0] ??
                for (int i = rowSpaceType.size-1; i > 0; i--) {
                    if (array[i].remainingCount != 0) {
                        next = array[i];
                        break;
                    }
                }
                RowRemainingSpacesCountPerSpaceType prev = next.prevRemaining;
                nextRemaining = next;
                prevRemaining = prev;
                prev.nextRemaining = this;
                next.prevRemaining = this;
            }
        }

        public int getRemainingCount() {
            return remainingCount;
        }

        public RowRemainingSpacesCountPerSpaceType getNextRemaining() {
            return nextRemaining;
        }

        public RowRemainingSpacesCountPerSpaceType getPrevRemaining() {
            return prevRemaining;
        }
        
        
	}
	
//	private final Map<Integer,RowRemainingSpacesCountPerSpaceType> spaceTypeCountPerSize = 
//	        new TreeMap<Integer,RowRemainingSpacesCountPerSpaceType>(Collections.reverseOrder());
	
	private final RowRemainingSpacesCountPerSpaceType[] spaceTypeCountPerSizeArray; // fully allocated... for optim

	private RowRemainingSpacesCountPerSpaceType sortedSpaceTypeCountListHead; // Head for sorted double-linked list 
    private RowRemainingSpacesCountPerSpaceType sortedSpaceTypeCountListTail; // Tail for sorted double-linked list
	
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

        // step1 ... determine max spaceType in slot sequence occurrences
        int maxSpace = 0;
        int seqStart = -1;
        for (int i = 0; i < rowSize; i++) {
            if (slotOccs[i].usable) {
                if (seqStart == -1) { // detected start of usable slots sequence
                    seqStart = i;
                }
            } else {
                if (seqStart != -1) { // detected end of usable slots sequence
                    int seqSize = i - seqStart + 1;
                    maxSpace = Math.max(maxSpace, seqSize);
                }
            }
        }
        // step 2: fully allocate all possible SpaceType [0 ... maxSpace]
        this.spaceTypeCountPerSizeArray = new RowRemainingSpacesCountPerSpaceType[maxSpace+1];
        spaceTypeCountPerSizeArray[0] = null; // useless?!
        for (int i = 1; i <= maxSpace; i++) {
            RowSpaceType rowSpaceType = new RowSpaceType(this, i);
            spaceTypeCountPerSizeArray[i] = new RowRemainingSpacesCountPerSpaceType(rowSpaceType);
        }
        
        
        // step 3 ... redo scan, to collect Slot Sequence occurrences
        seqStart = -1;
        for (int i = 0; i < rowSize; i++) {
            if (slotOccs[i].usable) {
                if (seqStart == -1) { // detected start of usable slots sequence
                    seqStart = i;
                }
            } else {
                if (seqStart != -1) { // detected end of usable slots sequence
                    int seqSize = i - seqStart + 1;
                    RowRemainingSpacesCountPerSpaceType countPerSpaceType = spaceTypeCountPerSizeArray[seqSize];
                    RowSpaceType rowSpaceType = countPerSpaceType.rowSpaceType;
                    
                    // register slot seq occ into SlotSequenceTypeCountPerRowAndSize
                    SlotSequenceOcc slotSeqOcc = new SlotSequenceOcc(rowSpaceType, seqStart);
                    slotSeqOccs.add(slotSeqOcc);
                    
                    countPerSpaceType.initAddOcc(slotSeqOcc); // => incr remainingCount
                    unassignedTotalRemainingSize += seqSize;
                }
            }
        }
        
        // step 4 ... double-link list entries node.prevRemaining, node.nextRemaining ...
        sortedSpaceTypeCountListHead = new RowRemainingSpacesCountPerSpaceType(new RowSpaceType(this, 0)); 
        sortedSpaceTypeCountListTail = new RowRemainingSpacesCountPerSpaceType(new RowSpaceType(this, maxSpace+1));
        RowRemainingSpacesCountPerSpaceType prevEntry = sortedSpaceTypeCountListHead;
        for (int i = maxSpace; i > 0; i--) {
            RowRemainingSpacesCountPerSpaceType e = spaceTypeCountPerSizeArray[i];
            if (e.remainingCount == 0) continue;
            prevEntry.nextRemaining = e;
            e.prevRemaining = prevEntry;
            prevEntry = e;
        }
        prevEntry.nextRemaining = sortedSpaceTypeCountListTail;
        sortedSpaceTypeCountListTail.prevRemaining = prevEntry;
	}

    // ------------------------------------------------------------------------

    public RowRemainingSpacesCountPerSpaceType getSpaceTypeCountPerSize(int i) {
        return spaceTypeCountPerSizeArray[i];
    }

    public RowRemainingSpacesCountPerSpaceType getSortedSpaceTypeCountListHead() {
        return sortedSpaceTypeCountListHead;
    }

    public RowRemainingSpacesCountPerSpaceType getSortedSpaceTypeCountListTail() {
        return sortedSpaceTypeCountListTail;
    }

    public void assignIncrServerTypeToSpaceType(int spaceSize, ServerType st, int stCount) {
        RowRemainingSpacesCountPerSpaceType count = spaceTypeCountPerSizeArray[spaceSize];
        count.assignIncr(st, stCount);

        int leftSpaceSize = spaceSize - st.size;
        assert leftSpaceSize >= 0;
        if (leftSpaceSize != 0) { 
            RowRemainingSpacesCountPerSpaceType leftSpaceCount = spaceTypeCountPerSizeArray[leftSpaceSize];
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

    public RowRemainingSpacesCountPerSpaceType[] getSpaceTypeCountPerSizeArray() {
        return spaceTypeCountPerSizeArray;
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
