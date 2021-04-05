package fr.an.tests.parquetmetadata.dto.parquet;

public enum ParquetPageType {
	DATA_PAGE (0),
	INDEX_PAGE (1),
	DICTIONARY_PAGE (2),
	DATA_PAGE_V2 (3);
	
	public final int value;

	private ParquetPageType(int value) {
		this.value = value;
	}
	
}