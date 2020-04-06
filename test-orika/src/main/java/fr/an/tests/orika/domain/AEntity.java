package fr.an.tests.orika.domain;

public class AEntity {

	private int id;
	
	private String name;

	private BEntity b;

	// --------------------------------------------------------------------------------------------
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public BEntity getB() {
		return b;
	}

	public void setB(BEntity b) {
		this.b = b;
	}
	
	
}
