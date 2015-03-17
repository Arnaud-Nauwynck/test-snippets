package fr.an.google.hashcode.fr2015.model;

import java.util.Comparator;

/**
 * immutable, value object
 *
 */
public final class ServerType {
    
    public static final Comparator<ServerType> BY_EFF_COMP = new Comparator<ServerType>() {
        @Override
        public int compare(ServerType s1, ServerType s2) {
            int res = Double.compare(s1.getEfficiency(), s2.getEfficiency());
            if (res != 0) return res;
            return Integer.compare(s1.getCapacity(), s2.getCapacity()); // TODO reverse?
        }
    };
    

    public static final Comparator<ServerType> BY_CAPACITTY_COMP = new Comparator<ServerType>() {
        @Override
        public int compare(ServerType s1, ServerType s2) {
            return Integer.compare(s1.getCapacity(), s2.getCapacity());
        }
    };
    
    
	public final int capacity;
	public final int size;
	
	// computed field:  capacity/size
	public final double efficiency;
	
	// ------------------------------------------------------------------------

	public ServerType(int capacity, int size) {
	    super();
	    this.capacity = capacity;
	    this.size = size;
	    
	    this.efficiency = capacity / size;
	}

	// ------------------------------------------------------------------------

	public int getCapacity() {
		return capacity;
	}

	public int getSize() {
		return size;
	}
	
	public double getEfficiency() {
	    return efficiency;
	}
	
    // ------------------------------------------------------------------------
    
	@Override
	public String toString() {
		return String.format("ServerType[cap=%s, size=%s, eff=%s]", capacity, size, efficiency);
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + capacity;
        long temp;
        temp = Double.doubleToLongBits(efficiency);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + size;
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
        ServerType other = (ServerType) obj;
        if (capacity != other.capacity)
            return false;
        if (Double.doubleToLongBits(efficiency) != Double.doubleToLongBits(other.efficiency))
            return false;
        if (size != other.size)
            return false;
        return true;
    }

	
}
