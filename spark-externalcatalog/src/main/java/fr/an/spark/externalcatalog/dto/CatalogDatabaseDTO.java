package fr.an.spark.externalcatalog.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Value;

@Data
public class CatalogDatabaseDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	String name;

	String description;

	URI locationUri;

	Map<String, String> properties;

	Map<String, CatalogTableDescDTO> tables = new HashMap<>();

	Map<String, CatalogFunctionDTO> functions = new HashMap<>();

	/**
	 * see corresponding spark scala : org.apache.spark.sql.catalyst.catalog.CatalogTable
	 */
	@Data
	public static class CatalogTableDescDTO implements Serializable {
		private static final long serialVersionUID = 1L;
		
		TableIdentifierKey identifier;
		CatalogTableTypeDTO tableType;
		CatalogStorageFormatDTO storage;
		
		// TODO (not serializable, neither json compatible..)
//	    schema: StructType,
		
	    String provider;
	    List<String> partitionColumnNames;
	    BucketSpecDTO bucketSpec;
	    String owner = "";
	    long createTime = System.currentTimeMillis();
	    long lastAccessTime = -1;
	    String createVersion = "";
	    Map<String,String> properties = new HashMap<>();
	    CatalogStatisticsDTO stats;
	    String viewText;
	    String comment;
	    List<String> unsupportedFeatures = new ArrayList<>();
	    boolean tracksPartitionsInCatalog = false;
	    boolean schemaPreservesCase = true;
	    Map<String,String> ignoredProperties = new LinkedHashMap<>();
	    String viewOriginalText;
	}

	@Value
	public static class TableIdentifierKey implements Serializable { // extends IdentifierWithDatabase
		private static final long serialVersionUID = 1L;

		public final String database;
		public final String table;
	}

//	@Value
//	public static class QualifiedTableNameKey

	// see CatalogTableType
	public static enum CatalogTableTypeDTO {
	  EXTERNAL, MANAGED, VIEW
	}

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
		
	// --------------------------------------------------------------------------------------------

	/**
	 *
	 */
	public static class CatalogFunctionDTO implements Serializable {
		private static final long serialVersionUID = 1L;

	}

}
