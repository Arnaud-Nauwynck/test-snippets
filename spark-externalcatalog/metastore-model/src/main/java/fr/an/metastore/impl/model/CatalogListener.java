package fr.an.metastore.impl.model;

public interface CatalogListener {

	public void onChange(DbCatalogEvent event);

	public abstract class DbCatalogEvent {
	}

}
