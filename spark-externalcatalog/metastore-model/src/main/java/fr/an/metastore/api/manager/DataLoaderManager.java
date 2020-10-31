package fr.an.metastore.api.manager;

public abstract class DataLoaderManager<TDb,TTable,TPart> {

	public abstract void loadTable(TDb db, TTable table, //
			String loadPath, boolean isOverwrite, boolean isSrcLocal);

	public abstract void loadPartition(TDb db, TTable table, TPart part, //
			String loadPath, boolean isOverwrite, boolean inheritTableSpecs, boolean isSrcLocal);

	public abstract void loadDynamicPartitions(TDb db, TTable table, TPart part, //
			String loadPath, boolean replace, int numDP);

}
