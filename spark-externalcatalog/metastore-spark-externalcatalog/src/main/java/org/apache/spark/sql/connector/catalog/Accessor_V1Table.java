package org.apache.spark.sql.connector.catalog;

import org.apache.spark.sql.catalyst.catalog.CatalogTable;

/**
 * HACK to access package protected class V1Table
 */
public class Accessor_V1Table {

	public static V1Table createV1Table(CatalogTable v1Table) {
		return new V1Table(v1Table);
	}
}
