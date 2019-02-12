package org.apache.hadoop.hdfs.server.namenode;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AddBlockOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AddCacheDirectiveInfoOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AddCachePoolOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AddErasureCodingPolicyOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AddOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AllocateBlockIdOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AllowSnapshotOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AppendOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.CancelDelegationTokenOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.ClearNSQuotaOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.CloseOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.ConcatDeleteOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.CreateSnapshotOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.DeleteOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.DeleteSnapshotOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.DisableErasureCodingPolicyOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.DisallowSnapshotOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.EnableErasureCodingPolicyOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.EndLogSegmentOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.GetDelegationTokenOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.InvalidOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.MkdirOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.ModifyCacheDirectiveInfoOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.ModifyCachePoolOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.ReassignLeaseOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RemoveCacheDirectiveInfoOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RemoveCachePoolOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RemoveErasureCodingPolicyOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RemoveXAttrOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RenameOldOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RenameOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RenameSnapshotOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RenewDelegationTokenOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RollingUpgradeFinalizeOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.RollingUpgradeStartOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetAclOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetGenstampV1Op;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetGenstampV2Op;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetNSQuotaOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetOwnerOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetPermissionsOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetQuotaByStorageTypeOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetQuotaOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetReplicationOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetStoragePolicyOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetXAttrOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.StartLogSegmentOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SymlinkOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.TimesOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.TruncateOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.UpdateBlocksOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.UpdateMasterKeyOp;

import lombok.Value;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FsEditDumpTool {

	private static final Pattern editFileNamePattern = Pattern.compile("edits_(\\d*)-(\\d*)");

	@Value
	private static class FirstLastTxId {
		public final long firstTxId;
		public final long lastTxId;
		
		public static FirstLastTxId parseFromFilename(String filename) {
			Matcher m = editFileNamePattern.matcher(filename);
			if (!m.matches()) {
				return null;
			}
			long firstTxId = Long.parseLong(m.group(1));
			long lastTxId = Long.parseLong(m.group(2));
			return new FirstLastTxId(firstTxId, lastTxId);
		}
	}

	

	
	public static void scanDumpEditFiles(File dir, long scanFromTxId, long scanToTxId) {
		File[] scanEditFiles = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return editFileNamePattern.matcher(name).matches();
			}
		});
		log.info("Found edits files:" + scanEditFiles.length);
		
		long currTxId = scanFromTxId;
		for(;;) {
			File currEditFile = findFirstEditFileWithTxId(scanEditFiles, currTxId);
			if (currEditFile == null) {
				log.info("end scanning edits file");
				break;
			}
			val editFileTxInfo = FirstLastTxId.parseFromFilename(currEditFile.getName());

			println("######### editFile: " + currEditFile);
			dumpEditFile(currEditFile, scanFromTxId, scanToTxId);
		
			if (scanToTxId != -1 && editFileTxInfo.lastTxId > scanToTxId) {
				break;
			}
			currTxId = editFileTxInfo.lastTxId + 1;
		}
	}
	
	public static File findFirstEditFileWithTxId(File[] files, long txId) {
		for(File file : files) {
			val editFileTxInfo = FirstLastTxId.parseFromFilename(file.getName());
			if (editFileTxInfo == null) {
				continue;
			}
			boolean hasTx = txId >= editFileTxInfo.firstTxId && txId <= editFileTxInfo.lastTxId;
			if (hasTx) {
				return file;
			}
		}
		return null;
	}
	
	public static void dumpEditFile(File editLogFile, long scanFromTxId, long scanToTxId) {
		val editFileTxInfo = FirstLastTxId.parseFromFilename(editLogFile.getName());

		try (EditLogInputStream in = new EditLogFileInputStream(editLogFile, 
				editFileTxInfo.firstTxId, editFileTxInfo.lastTxId, false)) {
			
			FSEditLogOp editOp = null;
			while(null != (editOp = in.readOp())) {

				if (editOp.hasTransactionId()) {
					long txId = editOp.getTransactionId();
					if (txId < scanFromTxId ||
							(scanToTxId != -1 && txId > scanToTxId)) {
						continue;
					}
				}

//				String displaySummary = "op ";
//				if (editOp.hasTransactionId()) {
//					long txId = editOp.getTransactionId();
//					displaySummary += " txId: " + txId;
//				}
//				if (editOp.hasRpcIds()) {
//					byte[] clientId = editOp.getClientId();
//					int callId = editOp.getCallId();
//					displaySummary += "   clientId: " + clientId + ", callId: " + callId);
//				}
//				System.out.println(displaySummary);
				
				visitEditOp(editOp);
				
				System.out.println();
			}
			
		} catch(Exception ex) {
			log.error("Failed to parse editlog", ex);
		}
	}

	private static void visitEditOp(FSEditLogOp editOp) {
		boolean usePrintToString = true;
		
		// if (editOp instanceof AddCloseOp) {
		  if (editOp instanceof AddOp) {
			  // ..
		  } else if (editOp instanceof CloseOp) {
			  // ..
		  } else if (editOp instanceof AppendOp) {
			  // 
//			    String path;
//			    String clientName;
//			    String clientMachine;
//			    boolean newBlock;

		  } else if (editOp instanceof AddBlockOp) {
//			    private String path;
//			    private Block penultimateBlock;
//			    private Block lastBlock;

		  } else if (editOp instanceof UpdateBlocksOp) {
		  } else if (editOp instanceof SetReplicationOp) {
		  } else if (editOp instanceof ConcatDeleteOp) {
		  } else if (editOp instanceof RenameOldOp) {
		  } else if (editOp instanceof DeleteOp) {
		  } else if (editOp instanceof MkdirOp) {
		  } else if (editOp instanceof SetGenstampV1Op) {
		  } else if (editOp instanceof SetGenstampV2Op) {
		  } else if (editOp instanceof AllocateBlockIdOp) {
		  } else if (editOp instanceof SetPermissionsOp) {
		  } else if (editOp instanceof SetOwnerOp) {
		  } else if (editOp instanceof SetNSQuotaOp) {
		  } else if (editOp instanceof ClearNSQuotaOp) {
		  } else if (editOp instanceof SetQuotaOp) {
		  } else if (editOp instanceof SetQuotaByStorageTypeOp) {
		  } else if (editOp instanceof TimesOp) {
		  } else if (editOp instanceof SymlinkOp) {
		  } else if (editOp instanceof RenameOp) {
		  } else if (editOp instanceof TruncateOp) {
		  } else if (editOp instanceof ReassignLeaseOp) {
		  } else if (editOp instanceof GetDelegationTokenOp) {
		  } else if (editOp instanceof RenewDelegationTokenOp) {
		  } else if (editOp instanceof CancelDelegationTokenOp) {
		  } else if (editOp instanceof UpdateMasterKeyOp) {
		  // abstract class LogSegmentOp                                         
		  } else if (editOp instanceof StartLogSegmentOp) {
		  } else if (editOp instanceof EndLogSegmentOp) {
		  } else if (editOp instanceof InvalidOp) {
		  } else if (editOp instanceof CreateSnapshotOp) {
		  } else if (editOp instanceof DeleteSnapshotOp) {
		  } else if (editOp instanceof RenameSnapshotOp) {
		  } else if (editOp instanceof AllowSnapshotOp) {
		  } else if (editOp instanceof DisallowSnapshotOp) {
		  } else if (editOp instanceof AddCacheDirectiveInfoOp) {
		  } else if (editOp instanceof ModifyCacheDirectiveInfoOp) {
		  } else if (editOp instanceof RemoveCacheDirectiveInfoOp) {
		  } else if (editOp instanceof AddCachePoolOp) {
		  } else if (editOp instanceof ModifyCachePoolOp) {
		  } else if (editOp instanceof RemoveCachePoolOp) {
		  } else if (editOp instanceof RemoveXAttrOp) {
		  } else if (editOp instanceof SetXAttrOp) {
		  } else if (editOp instanceof SetAclOp) {
		  } else if (editOp instanceof AddErasureCodingPolicyOp) {
		  } else if (editOp instanceof EnableErasureCodingPolicyOp) {
		  } else if (editOp instanceof DisableErasureCodingPolicyOp) {
		  } else if (editOp instanceof RemoveErasureCodingPolicyOp) {
		  } else if (editOp instanceof SetStoragePolicyOp) {
		  } else if (editOp instanceof RollingUpgradeStartOp) {
		  } else if (editOp instanceof RollingUpgradeFinalizeOp) {
		                                                                                                       
			  
		} else {
			System.out.println("unrecognized op type");
		}

		if (usePrintToString) {
			println(editOp.toString());
		}
	}

	private static void println(String text) {
		System.out.println(text);
	}
}
