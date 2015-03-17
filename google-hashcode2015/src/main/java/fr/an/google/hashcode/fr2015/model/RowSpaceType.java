package fr.an.google.hashcode.fr2015.model;


public final class RowSpaceType {
    
    public final Row row;
    public final int size;
    
    public RowSpaceType(Row row, int size) {
        this.row = row;
        this.size = size;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((row == null) ? 0 : row.hashCode());
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
        RowSpaceType other = (RowSpaceType) obj;
        if (row == null) {
            if (other.row != null)
                return false;
        } else if (!row.equals(other.row))
            return false;
        if (size != other.size)
            return false;
        return true;
    }
    
}