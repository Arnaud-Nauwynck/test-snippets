package fr.an.metastore.api.spi;

public interface CatalogListener {

	public void onChange(DbCatalogEvent event);

	public abstract class DbCatalogEvent {
	}

}
