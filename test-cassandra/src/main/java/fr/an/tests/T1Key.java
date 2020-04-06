package fr.an.tests;

import java.io.Serializable;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class T1Key implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private final int f1;

	@PrimaryKeyColumn(ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private final int f2;
	
	// ------------------------------------------------------------------------
	
	public T1Key(int f1, int f2) {
		this.f1 = f1;
		this.f2 = f2;
	}

	// ------------------------------------------------------------------------

	public int getF1() {
		return f1;
	}

	public int getF2() {
		return f2;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + f1;
		result = prime * result + f2;
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
		T1Key other = (T1Key) obj;
		if (f1 != other.f1)
			return false;
		if (f2 != other.f2)
			return false;
		return true;
	}
	
}
