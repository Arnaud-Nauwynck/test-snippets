package fr.an.metastore.api.immutable;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

/**
 * see corresponding spark scala :
 * org.apache.spark.sql.catalyst.catalog.CatalogTable
 */
@Value
@Builder
public class ImmutableCatalogTableDef implements Serializable {

	private static final long serialVersionUID = 1L;

	public final ImmutableTableId identifier;
	
	public final CatalogTableTypeEnum tableType;
	
	public final ImmutableCatalogStorageFormat storage;

	public final ImmutableStructType schema;

	public final String provider;
	
	public final ImmutableList<String> partitionColumnNames;
	
	public final ImmutableBucketSpec bucketSpec;
	
	public final String owner;
	public final long createTime;
	// not in def.. public long lastAccessTime;
	public final String createVersion;
	public final ImmutableMap<String, String> properties;
	// not in def.. CatalogStatisticsDTO stats;
	public final String viewText;
	public final String comment;
	public final ImmutableList<String> unsupportedFeatures;
	public final boolean tracksPartitionsInCatalog; // = false;
	public final boolean schemaPreservesCase; // = true;
	public final ImmutableMap<String, String> ignoredProperties;
	public final String viewOriginalText;

	// --------------------------------------------------------------------------------------------
	
	@Value @Builder
	public static class ImmutableCatalogStorageFormat {
		public final URI locationUri;
	    public final String inputFormat;
	    public final String outputFormat;
	    public final String serde;
	    public final boolean compressed;
	    public final ImmutableMap<String, String> properties;
	}

	@Value @Builder
	public static class ImmutableBucketSpec {
		public final int numBuckets;
		public final ImmutableList<String> bucketColumnNames;
		public final ImmutableList<String> sortColumnNames;
	}

	@Value @Builder
	public static class ImmutableCatalogStatistics {
		public final BigInteger sizeInBytes;
		public final BigInteger rowCount;
		public final ImmutableMap<String, ImmutableCatalogColumnStat> colStats;
	}
	    
	@Value @Builder
	public static class ImmutableCatalogColumnStat {
		public final BigInteger distinctCount;
		public final String min;
		public final String max;
		public final BigInteger nullCount;
		public final Long avgLen;
		public final Long maxLen;
		public final ImmutableHistogram histogram;
		public final int version; // = CatalogColumnStat.VERSION;
	}
	
	@Data
	public static class ImmutableHistogram {
		public final BigInteger sizeInBytes;
		public final BigInteger rowCount;
	    // TODO
//	    public final ImmutableAttributeMap<ColumnStat> attributeStats;
	}
	

}