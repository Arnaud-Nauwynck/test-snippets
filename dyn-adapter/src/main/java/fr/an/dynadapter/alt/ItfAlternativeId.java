package fr.an.dynadapter.alt;

public final class ItfAlternativeId<T> {

    /**
     * the interfaceId
     */
    public final ItfId<T> itfId;
    
    /**
     * the alternative name
     */
    public final String alternativeName;

    // ------------------------------------------------------------------------
    
    public ItfAlternativeId(ItfId<T> itfId, String alternativeName) {
        if (itfId == null || alternativeName == null) throw new IllegalArgumentException();
        this.itfId = itfId;
        this.alternativeName = alternativeName;
    }

    // ------------------------------------------------------------------------
    
    public ItfId<T> getItfId() {
        return itfId;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    @Override
    public int hashCode() {
        return itfId.hashCode() ^ alternativeName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItfAlternativeId<?> other = (ItfAlternativeId<?>) obj;
        return itfId.equals(other.itfId) && alternativeName.equals(other.alternativeName);
    }
    
}
