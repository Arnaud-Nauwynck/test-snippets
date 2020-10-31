package fr.an.metastore.impl.model;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStorageFormatDTO;
import fr.an.metastore.api.dto.CatalogTableDTO.HistogramDTO;
import fr.an.metastore.api.immutable.CatalogTableTypeEnum;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data @EqualsAndHashCode(callSuper=true)
public class TableModel extends ModelElement {

	public static enum TableModelChildField {
		partition
	}
	
	@Getter
	private final DatabaseModel db;
	@Getter
	private final String tableName;
	
	private final CatalogTableTypeEnum tableType;
	
	CatalogStorageFormatDTO storage;
	
//    schema: StructType,
//    provider: Option[String] = None,
    final List<String> partitionColumnNames = new ArrayList<>();
//    bucketSpec: Option[BucketSpec] = None,
	String owner = "";
	long createTime = System.currentTimeMillis();
	long lastAccessTime = -1;
//    createVersion: String = "",
//    properties: Map[String, String] = Map.empty,
//    stats: Option[CatalogStatistics] = None,
//    viewText: Option[String] = None,
//    comment: Option[String] = None,
//    unsupportedFeatures: Seq[String] = Seq.empty,
//    tracksPartitionsInCatalog: Boolean = false,
//    schemaPreservesCase: Boolean = true,
//    ignoredProperties: Map[String, String] = Map.empty,
//    viewOriginalText: Option[String] = None) {

    	
	private Map<ImmutablePartitionSpec, TablePartitionModel> partitions = new HashMap<>();
	
	// --------------------------------------------------------------------------------------------

	public TableModel(DatabaseModel db, String tableName, 
			CatalogTableTypeEnum tableType,
			//  storage: CatalogStorageFormat,
			//  schema: StructType,
			//  provider: Option[String] = None,
			List<String> partitionColumnNames
			) {
		this.db = db;
		this.tableName = tableName;
		this.tableType = tableType;
		if (partitionColumnNames != null) {
			this.partitionColumnNames.addAll(partitionColumnNames);
		}
	}

	// implements ModelElement
	// --------------------------------------------------------------------------------------------

	@Override
	public ModelElement getParent() {
		return db;
	}

	@Override
	public Object getParentField() {
		return DatabaseModel.DatabaseModelChildField.table;
	}
	
	@Override
	public String childId() {
		return tableName;
	}

	// --------------------------------------------------------------------------------------------

	public TablePartitionModel findPartition(ImmutablePartitionSpec spec) {
		return partitions.get(spec);
	}

	public List<TablePartitionModel> listPartitionsByPartialSpec(ImmutablePartitionSpec partialSpec) {
		if (partialSpec == null || partialSpec.isEmpty()) {
			return new ArrayList<>(partitions.values());
		}
		// TODO
		throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
	}

//	public List<TablePartitionModel> listPartitionsByFilter(
//			List<Expression> predicates, String defaultTimeZoneId) {
//		// TODO
//		throw new UnsupportedOperationException("TODO NOT IMPLEMENTED YET");
//	}

	public void addPartition(TablePartitionModel part) {
		partitions.put(part.getSpec(), part);
	}

	public void removePartition(TablePartitionModel part) {
		partitions.remove(part.getSpec());
	}

	// --------------------------------------------------------------------------------------------

	@Data
	public static class CatalogStorageFormatModel 
		// TODO extends ModelElement 
		{

		URI locationUri;
	    String inputFormat;
	    String outputFormat;
	    String serde;
	    boolean compressed;
	    Map<String, String> properties;
	}

	@Data
	public static class BucketSpecModel 
		// TODO extends ModelElement 
		{

		int numBuckets;
	    List<String> bucketColumnNames;
	    List<String> sortColumnNames;
	}

	@Data
	public static class CatalogStatisticsModel 
	// TODO extends ModelElement 
		{

	    BigInteger sizeInBytes;
	    BigInteger rowCount;
	    Map<String, CatalogColumnStatModel> colStats = new LinkedHashMap<>();
	}
	    
	@Data
	public static class CatalogColumnStatModel 
	// TODO extends ModelElement 
	{
	
		BigInteger distinctCount;
		String min;
		String max;
		BigInteger nullCount;
		Long avgLen;
		Long maxLen;
		HistogramDTO histogram;
		int version; // = CatalogColumnStat.VERSION;
	}
	
	@Data
	public static class HistogramModel
	// TODO extends ModelElement 
	{
		
	    BigInteger sizeInBytes;
	    BigInteger rowCount;
	    // TODO
//	    attributeStats: AttributeMap[ColumnStat] = AttributeMap(Nil)) {

	}
	
}
