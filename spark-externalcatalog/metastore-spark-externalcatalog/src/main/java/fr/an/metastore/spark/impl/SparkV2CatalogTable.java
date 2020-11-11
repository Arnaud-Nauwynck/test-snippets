package fr.an.metastore.spark.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.spark.sql.connector.catalog.TableCapability;
import org.apache.spark.sql.connector.expressions.LogicalExpressions;
import org.apache.spark.sql.connector.expressions.Transform;
import org.apache.spark.sql.types.StructType;

import fr.an.metastore.api.immutable.ImmutableCatalogTableDef;
import fr.an.metastore.api.immutable.ImmutableStructType;
import fr.an.metastore.api.utils.NotImpl;
import fr.an.metastore.spark.util.ScalaCollUtils;
import fr.an.metastore.spark.util.SparkAvroSchemaConversionUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * adapter class for ImmutableCatalogTableDef -> v2 Table interface 
 * 
 * TODO ... need SupportsRead, SupportsWrite
 * 
 * cf corresponding (private[sql]) code in org.apache.spark.sql.connector.catalog.V1Table
 *
 */
@Deprecated // currently replaced by Accessor_V1Table.createV1Table()
@RequiredArgsConstructor
public class SparkV2CatalogTable implements org.apache.spark.sql.connector.catalog.Table 
	// SupportsRead, SupportsWrite
	{

	private final ImmutableCatalogTableDef def;

	@Override
	public String name() {
		return def.identifier.table;
//		  override def name: String = v1Table.identifier.quoted
	}

	@Override
	public StructType schema() {
		return SparkAvroSchemaConversionUtils.schemaDefToSparkStructType(def.schema);
	}

	@Override
	public Set<TableCapability> capabilities() {
		return new java.util.HashSet<TableCapability>();
	}

	
	// @Override ??
	public Map<String, String> options() {
		val res = new HashMap<String, String>(def.storage.properties);
		if (def.storage.locationUri != null) {
			res.put("path", def.storage.locationUri.toString());
		}
		return res;
	}

	@Override
	public Map<String, String> properties() {
		return def.properties;
	}
	
	private Transform[] lazyPartitioning;
	
	@Override
	public Transform[] partitioning() {
		if (lazyPartitioning == null) {
			lazyPartitioning = doPartitioning();
		}
		return lazyPartitioning;
	}

	private Transform[] doPartitioning() {
		val res = new ArrayList<Transform>();
		if (def.partitionColumnNames != null) {
			for(val col : def.partitionColumnNames) {
				res.add(LogicalExpressions.identity(LogicalExpressions.reference(
						ScalaCollUtils.toScalaSeq(Arrays.asList(col)))));
			}
		}
		if (def.bucketSpec != null) {
			// cf spark code:
			// v1Table.bucketSpec.foreach { spec =>
			//   partitions += spec.asTransform
			// }
			for(val col : def.bucketSpec.bucketColumnNames) {
				res.add(LogicalExpressions.identity(LogicalExpressions.reference(
						ScalaCollUtils.toScalaSeq(Arrays.asList(col)))));
			}
		}
		return res.toArray(new Transform[res.size()]);
	}

	@Override
	public String toString() {
		return "SparkTable(" + name() + ")";
	}
	
}
