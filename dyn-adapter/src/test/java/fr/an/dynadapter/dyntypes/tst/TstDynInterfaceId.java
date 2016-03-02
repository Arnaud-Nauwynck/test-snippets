package fr.an.dynadapter.dyntypes.tst;

public final class TstDynInterfaceId {
    
    public final String name;
    
    public TstDynInterfaceId(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TstDynInterfaceId other = (TstDynInterfaceId) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    
}
