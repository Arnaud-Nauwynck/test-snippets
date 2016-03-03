package fr.an.dynadapter.alt;

public final class ItfId<T> {

    /**
     * java type for adapter "interface" (may be a class, not an interface.. even java.lang.Object)
     */
    public final Class<T> interfaceClass;
    
    /**
     * interface name, of empty for default (when java interfaceClass is not ambiguous).
     * A typical usage would be to use interfaceClass=java.lang.Runnable.class then put the real semantic name here,
     * as for example "start", "stop", "reload", ...  
     */
    public final String name;

    // ------------------------------------------------------------------------
    
    public ItfId(Class<T> interfaceClass) {
        this(interfaceClass, "");
    }
    
    public ItfId(Class<T> interfaceClass, String name) {
        if (interfaceClass == null || name == null) throw new IllegalArgumentException();
        this.interfaceClass = interfaceClass;
        this.name = name;
    }

    // ------------------------------------------------------------------------
    
    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return interfaceClass.hashCode() ^ name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItfId<?> other = (ItfId<?>) obj;
        return interfaceClass.equals(other.interfaceClass) && name.equals(other.name); 
    }

    @Override
    public String toString() {
        return interfaceClass.getName() + ((!name.isEmpty())? ":name=" + name : "");
    }
    
    
}
