package fr.an.metastore.impl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogModel extends ModelElement {

	public static enum CatalogChildField {
		database
	}
	
	private final Map<String,DatabaseModel> databases = new HashMap<>();

	// implements ModelElement
	// --------------------------------------------------------------------------------------------

	@Override
	public ModelElement getParent() {
		return null;
	}

	@Override
	public Object getParentField() {
		return null;
	}

	@Override
	public String childId() {
		return "rootCatalog";
	}
	
	// --------------------------------------------------------------------------------------------

	public DatabaseModel findDatabase(String db) {
		return databases.get(db);
	}

	public List<String> listDatabases() {
		return new ArrayList<>(databases.keySet());
	}

	public void addDatabase(DatabaseModel db) {
		databases.put(db.getName(), db);
	}

	public void removeDatabase(DatabaseModel db) {
		databases.remove(db.getName(), db);
	}

}
