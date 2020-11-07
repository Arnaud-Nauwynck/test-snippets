package fr.an.metastore.api.spi;

public abstract class DataLoader<TDb,TTable,TPart> {

	public abstract void loadTable(TDb db, TTable table, //
			String loadPath, boolean isOverwrite, boolean isSrcLocal);

	public abstract void loadPartition(TDb db, TTable table, TPart part, //
			String loadPath, boolean isOverwrite, boolean inheritTableSpecs, boolean isSrcLocal);

	public abstract void loadDynamicPartitions(TDb db, TTable table, TPart part, //
			String loadPath, boolean replace, int numDP);

	/**
	 * 
	 */
	public static class DoNothingDataLoader<TDb,TTable,TPart> extends DataLoader<TDb,TTable,TPart> {

		@Override
		public void loadTable(TDb db, TTable table, //
				String loadPath, boolean isOverwrite, boolean isSrcLocal) {
		}

		@Override
		public void loadPartition(TDb db, TTable table, TPart part, //
				String loadPath, boolean isOverwrite, boolean inheritTableSpecs, boolean isSrcLocal) {
		}

		@Override
		public void loadDynamicPartitions(TDb db, TTable table, TPart part, //
				String loadPath, boolean replace, int numDP) {
		}
	}

}
