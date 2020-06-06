package fr.an.tests.hivemetastoreclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hive.hcatalog.api.HCatClient;
import org.apache.hive.hcatalog.api.HCatCreateDBDesc;
import org.apache.hive.hcatalog.api.HCatCreateTableDesc;
import org.apache.hive.hcatalog.api.HCatTable;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;

public class HiveMetaStoreClientApp {

	public static void main(String[] args) {
		try {
			HiveMetaStoreClientApp app = new HiveMetaStoreClientApp();
			app.run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void run() throws HCatException {

		Configuration conf = new Configuration();
		
		HCatClient cli;
		try {
			cli = HCatClient.create(conf);
		} catch (HCatException ex) {
			throw new RuntimeException("Failed to create cli", ex);
		}
		
		runCli(cli);

		
	}

	private void runCli(HCatClient cli) throws HCatException {
		List<String> dbNames = cli.listDatabaseNamesByPattern(".*");
		System.out.println("found databases:" + dbNames);
		
		String testdb1 = "testdb1";
		if (dbNames.contains(testdb1)) {
			System.out.println("already contains database '" + testdb1 + "'");
		} else {
			HCatCreateDBDesc dbInfo = HCatCreateDBDesc.create(testdb1)
					.comment("comment for testdb1")
					// .location("hdfs:///testdb1")
					.build();
			cli.createDatabase(dbInfo);
		
			dbNames = cli.listDatabaseNamesByPattern(".*");
			
		}
		
//		String sql= "CREATE database testdb " 
//				+ "COMMENT \"comment1\" "
//				+ "LOCATION \"file://dir1\" "
//				+" WITH PROPERTIES (" 
//				+ " prop1 = \"value1\","
//				+ " prop2 = \"value2\""
//				+ ")";
	

		
		List<String> tables = cli.listTableNamesByPattern(testdb1, ".*");
		System.out.println("found tables in " + testdb1 + ":" + tables);

		if (tables.contains("employee")) {
			System.out.println("already contains table '" + testdb1 + ".employee'");
		} else {
			HCatTable table = new HCatTable(testdb1, "employee");
			
			ArrayList<HCatFieldSchema> cols = new ArrayList<HCatFieldSchema>();
			cols.add(new HCatFieldSchema("eid", HCatFieldSchema.Type.INT, "id columns"));
			cols.add(new HCatFieldSchema("name", HCatFieldSchema.Type.STRING, "id columns"));
			cols.add(new HCatFieldSchema("salary", HCatFieldSchema.Type.STRING, ""));
			table.cols(cols);
			
			HCatFieldSchema partCol = new HCatFieldSchema("department", HCatFieldSchema.Type.STRING, "");
			table.partCol(partCol);
			
			table.comment("Employee details");
			
			table.storageHandler("TEXTFILE");
			table.fieldsTerminatedBy('\t');
			table.linesTerminatedBy('\n');
			// table.serdeParam(serdeParams);
			
			String sql= "CREATE EXTERNAL TABLE IF NOT EXISTS db1.employee " + 
					"( eid int, name String, \n" + 
					"salary String, destination String)\n" + 
					"PARTITIONED BY (department String)\n" +
					"COMMENT 'Employee details'\n" + 
					"ROW FORMAT DELIMITED\n" + 
					"FIELDS TERMINATED BY '\\t'\n" + 
					"LINES TERMINATED BY '\\n'\n" + 
					"STORED AS TEXTFILE";
	
			cli.createTable(HCatCreateTableDesc.create(table).ifNotExists(true).build());

			tables = cli.listTableNamesByPattern(testdb1, ".*");

		}
		
	}
}
