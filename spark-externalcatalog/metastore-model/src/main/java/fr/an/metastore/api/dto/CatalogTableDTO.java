package fr.an.metastore.api.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.an.metastore.api.immutable.CatalogTableTypeEnum;
import fr.an.metastore.api.immutable.CatalogTableId;
import lombok.Data;

/**
 * see corresponding spark scala :
 * org.apache.spark.sql.catalyst.catalog.CatalogTable
 */
@Data
public class CatalogTableDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	CatalogTableId identifier;
	
	CatalogTableTypeEnum tableType;
	
	CatalogStorageFormatDTO storage;

	StructTypeDTO schema;

	String provider;
	
	List<String> partitionColumnNames;
	
	BucketSpecDTO bucketSpec;
	
	String owner = "";
	long createTime = System.currentTimeMillis();
	long lastAccessTime = -1;
	String createVersion = "";
	Map<String, String> properties = new HashMap<>();
	CatalogStatisticsDTO stats;
	String viewText;
	String comment;
	List<String> unsupportedFeatures = new ArrayList<>();
	boolean tracksPartitionsInCatalog = false;
	boolean schemaPreservesCase = true;
	Map<String, String> ignoredProperties = new LinkedHashMap<>();
	String viewOriginalText;

//		@Value
//		public static class QualifiedTableNameKey

	// --------------------------------------------------------------------------------------------
	@Data
	public static class CatalogStorageFormatDTO implements Serializable {
		private static final long serialVersionUID = 1L;

		URI locationUri;
	    String inputFormat;
	    String outputFormat;
	    String serde;
	    boolean compressed;
	    Map<String, String> properties;
	}

	@Data
	public static class BucketSpecDTO implements Serializable {
		private static final long serialVersionUID = 1L;

		int numBuckets;
	    List<String> bucketColumnNames;
	    List<String> sortColumnNames;
	}

	@Data
	public static class CatalogStatisticsDTO implements Serializable {
		private static final long serialVersionUID = 1L;

	    BigInteger sizeInBytes;
	    BigInteger rowCount;
	    Map<String, CatalogColumnStatDTO> colStats = new LinkedHashMap<>();
	}
	    
	@Data
	public static class CatalogColumnStatDTO implements Serializable {
		private static final long serialVersionUID = 1L;
	
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
	public static class HistogramDTO implements Serializable {
		private static final long serialVersionUID = 1L;
		
	    BigInteger sizeInBytes;
	    BigInteger rowCount;
	    // TODO
//	    attributeStats: AttributeMap[ColumnStat] = AttributeMap(Nil)) {

	}
	

}