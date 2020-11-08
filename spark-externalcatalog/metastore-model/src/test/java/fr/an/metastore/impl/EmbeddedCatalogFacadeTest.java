package fr.an.metastore.impl;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import fr.an.metastore.api.immutable.CatalogTableId;
import fr.an.metastore.api.immutable.ImmutableCatalogDatabaseDef;
import fr.an.metastore.api.immutable.ImmutableStructType.ImmutableStructField;
import lombok.val;

public class EmbeddedCatalogFacadeTest {

	@Test
	public void testEmptyDb1() {
		val cat = new EmbeddedCatalogFacade(new File("src/test/data/catalogs/catalog1.yml"));
		
		ImmutableCatalogDatabaseDef db1 = cat.getDatabase("db1");
		Assert.assertNotNull(db1);
		Assert.assertEquals("file:///src/test/data/test1", db1.locationUri.toString());
	}

	@Test
	public void testSimpleTableDb2() {
		val cat = new EmbeddedCatalogFacade(new File("src/test/data/catalogs/catalog2.yml"));
		
		ImmutableCatalogDatabaseDef db2 = cat.getDatabase("db2");
		Assert.assertNotNull(db2);
		Assert.assertEquals("file:///src/test/data/test2", db2.locationUri.toString());
		val table1Id = new CatalogTableId("db2", "table1");
		val table1 = cat.getTableDef(table1Id);
		Assert.assertNotNull(table1);
		
		val schema = table1.getSchema();
		val avroSchema = schema.getAsAvroSchema();
		Assert.assertNotNull(avroSchema);
		
		ImmutableList<ImmutableStructField> tableFields = schema.getFields();
		Assert.assertNotNull(tableFields);
		Assert.assertTrue(tableFields.size() >= 4);
		val field0 = tableFields.get(0);
		Assert.assertEquals("strField", field0.name);

		val field1 = tableFields.get(1);
		Assert.assertEquals("strNullableField", field1.name);
		
		val field2 = tableFields.get(2);
		Assert.assertEquals("intField", field2.name);
		
		val field3 = tableFields.get(3);
		Assert.assertEquals("intNullableField", field3.name);
	}
}
