package fr.an.tests;


import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class T1 {

	@PrimaryKey
	private T1Key pk;
	
	private String f3;
	private String f4;
	
	// ------------------------------------------------------------------------
	
	// silly constructor required by spring-data-cassandra WHY WHY?!! 
	// ... otherwise you have error "Property [pk] has no single column mapping"
	public T1() {
	}
	
	public T1(T1Key pk) {
		this.pk = pk;
	}

	// ------------------------------------------------------------------------

	// setter not needed pk from spring-data-cassandra!
	public T1Key getPK() {
		return pk;
	}
	
	// dummy destructured getters getF1() and getF2() for composite key	
	public int getF1() {
		return pk.getF1();
	}
	public int getF2() {
		return pk.getF2();
	}

	public String getF3() {
		return f3;
	}

	public void setF3(String f3) {
		this.f3 = f3;
	}

	public String getF4() {
		return f4;
	}

	public void setF4(String f4) {
		this.f4 = f4;
	}

}
