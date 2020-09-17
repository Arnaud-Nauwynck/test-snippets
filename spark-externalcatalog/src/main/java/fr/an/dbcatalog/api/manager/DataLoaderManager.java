package fr.an.dbcatalog.api.manager;

import fr.an.dbcatalog.api.PartitionSpec;

public abstract class DataLoaderManager<TDb,TTable> {

	public abstract void loadTable(TDb db, TTable table, String loadPath, boolean isOverwrite, boolean isSrcLocal);

	public abstract void loadPartition(TDb db, TTable table, String loadPath,
			PartitionSpec partition, boolean isOverwrite, boolean inheritTableSpecs,
			boolean isSrcLocal);

	public abstract void loadDynamicPartitions(TDb db, TTable table, String loadPath,
			PartitionSpec partition, boolean replace, int numDP);

}
