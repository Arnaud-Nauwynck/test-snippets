package fr.an.metastore.impl.model;

public abstract class ModelElement {

	public abstract ModelElement getParent();

	public abstract Object getParentField();
	
	public abstract String childId();

//	public abstract List<Object> listChildKind();
//	public abstract ModelElement findChild(Object childKind, String childId);

}
