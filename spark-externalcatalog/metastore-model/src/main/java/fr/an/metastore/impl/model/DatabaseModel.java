package fr.an.metastore.impl.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.spi.IDatabaseModel;
import fr.an.metastore.impl.model.CatalogModel.CatalogChildField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=false)
public class DatabaseModel extends ModelElement implements IDatabaseModel {

	public static enum DatabaseModelChildField {
		table, function
	}
	private final CatalogModel catalog;
	private final String name;
	
	private ImmutableCatalogDatabaseDef dbDef;
//	private String description;
//	private URI locationUri;
//	private final Map<String, String> properties = new LinkedHashMap<>();

	private final Map<String,TableModel> tables = new LinkedHashMap<>();

	private final Map<String,FunctionModel> functions = new LinkedHashMap<>();

	// --------------------------------------------------------------------------------------------

	public DatabaseModel(CatalogModel catalog, String name, ImmutableCatalogDatabaseDef dbDef) {
		this.catalog = catalog;
		this.name = name;
		this.dbDef = dbDef;
	}

	// implements ModelElement
	// --------------------------------------------------------------------------------------------

	@Override
	public ModelElement getParent() {
		return catalog;
	}

	@Override
	public Object getParentField() {
		return CatalogChildField.database;
	}

	@Override
	public String childId() {
		return name;
	}

	// --------------------------------------------------------------------------------------------

	public TableModel findTable(String tableName) {
		return tables.get(tableName);
	}

	public List<String> listTables() {
		return new ArrayList<>(tables.keySet());
	}

	public void addTable(TableModel tbl) {
		tables.put(tbl.getTableName(), tbl);
	}

	public void removeTable(TableModel tbl) {
		tables.remove(tbl.getTableName());
	}

	public FunctionModel findFunction(String funcName) {
		return functions.get(funcName);
	}

	public List<String> listFunctions() {
		return new ArrayList<>(functions.keySet());
	}

	public void addFunction(FunctionModel func) {
		functions.put(func.getFuncName(), func);
	}

	public void removeFunction(FunctionModel func) {
		functions.remove(func.getFuncName());
	}

	public String getDescription() {
		return dbDef.description;
	}

	public void setDescription(String p) {
		this.dbDef = dbDef.copyWithDescription(p);
	}

	public URI getLocationUri() {
		return dbDef.locationUri;
	}

	public void setLoactionUri(URI p) {
		this.dbDef = dbDef.copyWithLocationUri(p);
	}

	public ImmutableMap<String,String> getProperties() {
		return dbDef.properties;
	}

	public void setProperties(Map<String, String> p) {
		this.dbDef = dbDef.copyWithProperties(p);
	}

}
