package fr.an.dynadapter.tstdynobj;

public final class TstDynType {
    
    public final String name;
    private final TstDynType superType;
    private final TstDynType[] interfaces;
    
    public TstDynType(String name, TstDynType superType, TstDynType[] interfaces) {
        this.name = name;
        this.superType = superType;
        this.interfaces = interfaces != null? interfaces.clone() : new TstDynType[0];
    }
    
    public String getName() {
        return name;
    }

    public TstDynType getSuperType() {
        return superType;
    }

    public TstDynType[] getInterfaces() {
        return interfaces;
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
        TstDynType other = (TstDynType) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    
}
