package fr.an.google.hashcode.fr2015.model.occ;

import fr.an.google.hashcode.fr2015.model.RowSpaceType;

public final class SlotSequenceOcc {
    
    public final RowSpaceType rowSpaceType;
    
    public final int startSlotId;
    
    public SlotSequenceOcc(RowSpaceType rowSpaceType, int startSlotId) {
        this.rowSpaceType = rowSpaceType;
        this.startSlotId = startSlotId;
    }

    public int getSpaceSize() {
        return rowSpaceType.size;
    }
}