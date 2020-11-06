package fr.an.metastore.impl.model;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.an.metastore.api.dto.CatalogTableDTO.HistogramDTO;
import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data @EqualsAndHashCode(callSuper=true)
public class TableModel extends ModelElement {

	public static enum TableModelChildField {
		partition
	}
	
	@Getter
	private final DatabaseModel db;
	
	@Getter @Setter
	private ImmutableCatalogTableDef def;

	long lastAccessTime = -1;

	// TOCHANGE externalise.. can be huge, so not not in-memory, use persistent db, like LevelDB
	private Map<ImmutablePartitionSpec, TablePartitionModel> partitions = new HashMap<>();
	
	// --------------------------------------------------------------------------------------------

	public TableModel(DatabaseModel db, // String tableName, 
			ImmutableCatalogTableDef def
			) {
		this.db = db;
		this.def = def;
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
		return def.getIdentifier().table;
	}

	// --------------------------------------------------------------------------------------------

	public CatalogTableId getTableId() {
		return def.getIdentifier();
	}

	public String getTableName() {
		return def.getIdentifier().table;
	}

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
