package fr.an.google.hashcode.fr2015.model.occ;

import fr.an.google.hashcode.fr2015.model.Row;

public final class SlotOcc {

    public final Row row;
	public final int id;

	public final boolean usable;
	
	
	// private ServerOcc server;
	
   // ------------------------------------------------------------------------

	public SlotOcc(Row row, int id, boolean usable) {
		this.row = row;
		this.id = id;
		this.usable = usable;
	}
	
	// ------------------------------------------------------------------------

	public Row getRow() {
        return row;
    }

    public int getId() {
        return id;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((row == null) ? 0 : row.hashCode());
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
        SlotOcc other = (SlotOcc) obj;
        if (id != other.id)
            return false;
        if (row == null) {
            if (other.row != null)
                return false;
        } else if (!row.equals(other.row))
            return false;
        return true;
    }


}
