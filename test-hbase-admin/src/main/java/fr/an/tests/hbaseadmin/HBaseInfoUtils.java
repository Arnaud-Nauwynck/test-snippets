package fr.an.tests.hbaseadmin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.ClusterStatus;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.RegionLoad;
import org.apache.hadoop.hbase.ServerLoad;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.master.RegionState;
import org.apache.hadoop.hbase.master.RegionState.State;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos.SnapshotDescription;
import org.apache.hadoop.hbase.protobuf.generated.HBaseProtos.SnapshotDescription.Type;

import lombok.val;

public class HBaseInfoUtils {

	public static void list(HBaseAdmin admin) throws Exception {
		NamespaceDescriptor[] namespaceDescrs = admin.listNamespaceDescriptors();
		HTableDescriptor[] tableDescrs = admin.listTables();
		
		val tableNames = admin.listTableNames();
		for(TableName tableName : tableNames) {
			List<HRegionInfo> regionInfos = admin.getTableRegions(tableName);
			
			for(HRegionInfo regionInfo : regionInfos) {
				
			}
		}
		
		ServerName sn = null; // ???? cf ClusterState ? 
		List<HRegionInfo> onlineRegions = admin.getOnlineRegions(sn);
		for(HRegionInfo r : onlineRegions) {
			 TableName tableName = r.getTable();
			 
			  byte [] startKey = r.getStartKey();
			  byte [] endKey = r.getEndKey();
			  boolean offLine = r.isOffline();
			  long regionId = r.getRegionId();
			  String regionName = r.getRegionNameAsString();
			  boolean split = r.isSplit();
			  String encodedName = r.getEncodedName();

		 }
		
		List<SnapshotDescription> snapshots = admin.listSnapshots();
		for(SnapshotDescription snap : snapshots) {
			String name = snap.getName();
			
	        String table = snap.getTable();
	        long creationTime = snap.getCreationTime();
	        Type type = snap.getType();
	        int version = snap.getVersion();
	        
		}
	}
	
	public static void scanClusterStatus(HBaseAdmin admin) throws Exception {

		ClusterStatus cs = admin.getClusterStatus();
			  
		ServerName masterSN = cs.getMaster();
		Collection<ServerName> backupMasters = cs.getBackupMasters();

		Collection<ServerName> serverNames = cs.getServers();
		
		for(val sn : serverNames) {
			ServerLoad serverLoad = cs.getLoad(sn);
	
//			  private int stores = 0;
//			  private int storefiles = 0;
//			  private int storeUncompressedSizeMB = 0;
//			  private int storefileSizeMB = 0;
//			  private int memstoreSizeMB = 0;
//			  private int storefileIndexSizeMB = 0;
//			  private int readRequestsCount = 0;
//			  private int writeRequestsCount = 0;
//			  private int rootIndexSizeKB = 0;
//			  private int totalStaticIndexSizeKB = 0;
//			  private int totalStaticBloomSizeKB = 0;
//			  private long totalCompactingKVs = 0;
//			  private long currentCompactedKVs = 0;

			/* @return number of requests  since last report. */
			if (serverLoad.hasNumberOfRequests()) {
				long numberOfRequests = serverLoad.getNumberOfRequests();
			}
			if (serverLoad.hasTotalNumberOfRequests()) {
				int t = serverLoad.getTotalNumberOfRequests();
			}

			int usedHeap = serverLoad.getUsedHeapMB();

			int maxHeapMB = serverLoad.getMaxHeapMB();

			int numberOfRegions = serverLoad.getNumberOfRegions();

			int port = serverLoad.getInfoServerPort();


			Map<byte[], RegionLoad> regionsLoad = serverLoad.getRegionsLoad();
			for(Map.Entry<byte[],RegionLoad> e : regionsLoad.entrySet()) {
				RegionLoad rl = e.getValue();
				
				int stores = rl.getStores();
				int storeFiles = rl.getStorefiles();
				int StorefileSizeMB = rl.getStorefileSizeMB();
				int MemStoreSizeMB = rl.getMemStoreSizeMB();
				int StorefileIndexSizeMB = rl.getStorefileIndexSizeMB();
				long RequestsCount = rl.getRequestsCount();
				long ReadRequestsCount = rl.getReadRequestsCount();
				long WriteRequestsCount = rl.getWriteRequestsCount();
				int RootIndexSizeKB = rl.getRootIndexSizeKB();
				int TotalStaticIndexSizeKB = rl.getTotalStaticIndexSizeKB();
				int TotalStaticBloomSizeKB = rl.getTotalStaticBloomSizeKB();
				long TotalCompactingKVs = rl.getTotalCompactingKVs();
				long CurrentCompactedKVs = rl.getCurrentCompactedKVs();
				// long getCompleteSequenceId() {
				int StoreUncompressedSizeMB = rl.getStoreUncompressedSizeMB();

			}
			
			// public String[] getRegionServerCoprocessors();
			// String[] getRsCoprocessors() {

			double RequestsPerSecond = serverLoad.getRequestsPerSecond();
			  
		}

		Map<String, RegionState> regionsInTransition = cs.getRegionsInTransition();
		for(Map.Entry<String, RegionState> e : regionsInTransition.entrySet()) {
			RegionState rs = e.getValue();
			HRegionInfo hri = rs.getRegion();
			ServerName serverName = rs.getServerName();
			State state = rs.getState();

			  
		}
	}

}
