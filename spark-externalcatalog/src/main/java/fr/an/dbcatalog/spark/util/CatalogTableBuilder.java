package fr.an.dbcatalog.spark.util;

import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.catalog.BucketSpec;
import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogStorageFormat;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;
import org.apache.spark.sql.catalyst.catalog.CatalogTableType;
import org.apache.spark.sql.types.StructType;

import lombok.Getter;
import lombok.Setter;
import scala.Option;
import scala.collection.Seq;
import scala.collection.immutable.Map;

@Getter @Setter
public class CatalogTableBuilder {
	
	TableIdentifier identifier = null;
	CatalogTableType tableType = null;
	CatalogStorageFormat storage = null;
	StructType schema = null;
	Option<String> provider = null;
	Seq<String> partitionColumnNames = null;
	Option<BucketSpec> bucketSpec = null;
	String owner = null;
	long createTime = 0;
	long lastAccessTime = 0;
	String createVersion = null;
	Map<String, String> properties = null;
	Option<CatalogStatistics> stats = null;
	Option<String> viewText = null;
	Option<String> comment = null;
	Seq<String> unsupportedFeatures = null;
	boolean tracksPartitionsInCatalog = false;
	boolean schemaPreservesCase = false;
	Map<String, String> ignoredProperties = null;
	Option<String> viewOriginalText = null;

	
    
    public CatalogTableBuilder(CatalogTable t) {
		super();
		this.identifier = t.identifier();
		this.tableType = t.tableType();
		this.storage = t.storage();
		this.schema = t.schema();
		this.provider = t.provider();
		this.partitionColumnNames = t.partitionColumnNames();
		this.bucketSpec = t.bucketSpec();
		this.owner = t.owner();
		this.createTime = t.createTime();
		this.lastAccessTime = t.lastAccessTime();
		this.createVersion = t.createVersion();
		this.properties = t.properties();
		this.stats = t.stats();
		this.viewText = t.viewText();
		this.comment = t.comment();
		this.unsupportedFeatures = t.unsupportedFeatures();
		this.tracksPartitionsInCatalog = t.tracksPartitionsInCatalog();
		this.schemaPreservesCase = t.schemaPreservesCase();
		this.ignoredProperties = t.ignoredProperties();
		this.viewOriginalText = t.viewOriginalText();
	}
    
    public CatalogTable build() {
    	return new CatalogTable(identifier, tableType, storage, schema, provider, partitionColumnNames, bucketSpec, owner, createTime, lastAccessTime, createVersion, properties, stats, viewText, comment, unsupportedFeatures, tracksPartitionsInCatalog, schemaPreservesCase, ignoredProperties, viewOriginalText);
    }
    
}