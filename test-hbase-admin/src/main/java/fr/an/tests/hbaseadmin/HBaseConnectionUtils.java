package fr.an.tests.hbaseadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.protobuf.generated.AdminProtos.AdminService;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos.ClientService;
import org.apache.hadoop.hbase.protobuf.generated.MasterProtos.MasterService;

public class HBaseConnectionUtils {

	public static void scan() throws Exception {
		Configuration conf = HBaseConfiguration.create();
		HConnection conn = HConnectionManager.createConnection(conf);

		TableName tableName = null;
		List<HRegionLocation> regionLocs = conn.locateRegions(tableName);
		for(HRegionLocation regionLoc : regionLocs) {
			  HRegionInfo regionInfo = regionLoc.getRegionInfo();
			  ServerName serverName = regionLoc.getServerName();
			  long seqNum = regionLoc.getSeqNum();
		}
		
		
		ServerName serverName = null;
		MasterService.BlockingInterface connMaster = conn.getMaster();
		AdminService.BlockingInterface connAdmin = conn.getAdmin(serverName);
		ClientService.BlockingInterface connClient = conn.getClient(serverName);

		HTableInterface htable = conn.getTable(tableName);
		
		List<? extends Row> actions = new ArrayList<>();
		Object[] results = new Object[1];
		htable.batch(actions, results);
		
		List<Put> puts = new ArrayList<>();
		htable.put(puts);
	}
}
