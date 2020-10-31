package fr.an.metastore.spark.util;

import org.apache.spark.sql.catalyst.catalog.CatalogStatistics;
import org.apache.spark.sql.catalyst.catalog.CatalogStorageFormat;
import org.apache.spark.sql.catalyst.catalog.CatalogTablePartition;

import scala.Option;

public class CatalogTablePartitionBuilder {
	
	scala.collection.immutable.Map<String,String> spec;
	CatalogStorageFormat storage;
	scala.collection.immutable.Map<String,String> parameters;
	long createTime;
	long lastAccessTime;
	Option<CatalogStatistics> stats;
	
	
	public CatalogTablePartitionBuilder(CatalogTablePartition src) {
		this.spec = src.spec();
		this.storage = src.storage();
		this.parameters = src.parameters();
		this.createTime = src.createTime();
		this.lastAccessTime = src.lastAccessTime();
		this.stats = src.stats();
	}
	
	public CatalogTablePartition build() {
		return new CatalogTablePartition(spec, storage, parameters, createTime, lastAccessTime, stats);
	}
	
}
