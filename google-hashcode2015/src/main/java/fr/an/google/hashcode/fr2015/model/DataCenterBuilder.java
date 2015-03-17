package fr.an.google.hashcode.fr2015.model;

import java.util.ArrayList;
import java.util.List;


public class DataCenterBuilder {

    /**
     * immutable, value object for server info
     * used to build DataCenter
     */
    public static final class ServerInfo {

        public final int size;
        public final int capacity;
        
        public ServerInfo(int size, int capacity) {
            this.size = size;
            this.capacity = capacity;
        }
        
    }

    /**
     * immutable, value object for unavailable slot info
     * used to build DataCenter
     */
    public static final class UnavailableSlotInfo {
        
        public final int rowId;
        public final int slotId;
        
        public UnavailableSlotInfo(int rowId, int slotId) {
            this.rowId = rowId;
            this.slotId = slotId;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + rowId;
            result = prime * result + slotId;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            UnavailableSlotInfo other = (UnavailableSlotInfo) obj;
            if (rowId != other.rowId)
                return false;
            if (slotId != other.slotId)
                return false;
            return true;
        }
        
    }
    
    // ------------------------------------------------------------------------

    
    public int rowsCount;
    public int rowSize;
    public int groupsCount;
    public int serversCount;
    private List<UnavailableSlotInfo> unavailableSlotInfos = new ArrayList<UnavailableSlotInfo>(); 
    private List<ServerInfo> serversData = new ArrayList<ServerInfo>();
    
    // ------------------------------------------------------------------------

    public DataCenterBuilder() {
    }
    
    // ------------------------------------------------------------------------
    
    public DataCenter build() {
        return new DataCenter(rowsCount, rowSize, groupsCount, serversCount, unavailableSlotInfos, serversData);
    }

    public void addUnavailableSlotInfo(UnavailableSlotInfo elt) {
        this.unavailableSlotInfos.add(elt);
    }

    public void addServerData(ServerInfo elt) {
        this.serversData.add(elt);
    }

    
}
