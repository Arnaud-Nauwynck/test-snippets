package fr.an.metastore.impl.model;

public interface DbCatalogListener {

	public void onChange(DbCatalogEvent event);

	public abstract class DbCatalogEvent {
	}

}
