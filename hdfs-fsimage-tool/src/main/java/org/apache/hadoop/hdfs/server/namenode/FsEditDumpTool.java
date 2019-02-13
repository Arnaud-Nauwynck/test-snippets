package org.apache.hadoop.hdfs.server.namenode;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hdfs.server.namenode.AbstractFsChange.AddFileFsChange;
import org.apache.hadoop.hdfs.server.namenode.AbstractFsChange.CompositeFsPathChange;
import org.apache.hadoop.hdfs.server.namenode.AbstractFsChange.DeleteFileFsChange;
import org.apache.hadoop.hdfs.server.namenode.AbstractFsChange.MkdirFsChange;
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

	private Map<String,AbstractFsChange> currPathChange = new LinkedHashMap<>();
	private Map<String,DeleteFileFsChange> pathDeletedChg = new LinkedHashMap<>();
	
	
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

	

	
	public void scanDumpEditFiles(File dir, long scanFromTxId, long scanToTxId) {
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

		log.info("dumping accumulated file changes:");
		for(Map.Entry<String,AbstractFsChange> e : currPathChange.entrySet()) {
			System.out.println("path: " + e.getKey());
			AbstractFsChange chg = e.getValue();
			if (chg instanceof CompositeFsPathChange) {
				CompositeFsPathChange opsChg = (CompositeFsPathChange) chg;
				System.out.print(", " + opsChg.ops.size() + " ops: ");
				for(val op : opsChg.ops) {
					System.out.print(op.summaryString() + ", ");
				}
				System.out.println();
			} else {
				System.out.println("?" + chg);
			}
		}
		
		log.info("dumping accumulated file deletes:");
		for(Map.Entry<String,DeleteFileFsChange> e : pathDeletedChg.entrySet()) {
			System.out.println("delete " + e.getKey());
		}

	}
	
	public File findFirstEditFileWithTxId(File[] files, long txId) {
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
	
	public void dumpEditFile(File editLogFile, long scanFromTxId, long scanToTxId) {
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
			}
			
		} catch(Exception ex) {
			log.error("Failed to parse editlog", ex);
		}
	}

	private void visitEditOp(FSEditLogOp editOp) {
		boolean usePrintToString = true;
		boolean handleChg = false;
		
		// if (editOp instanceof AddCloseOp) {
		  if (editOp instanceof AddOp) {
			  AddOp op = (AddOp) editOp;
			  String path = op.getPath();
			  CompositeFsPathChange chg = pathChangeOrCreate(path);
			  chg.addOp(op);
			  handleChg = true;
		  } else if (editOp instanceof CloseOp) {
			  CloseOp op = (CloseOp) editOp;
			  String path = op.getPath();
			  CompositeFsPathChange chg = pathChangeOrCreate(path);
			  chg.addCloseOp(op);
			  handleChg = true;
		  } else if (editOp instanceof AppendOp) {
			  // 
//			    String path;
//			    String clientName;
//			    String clientMachine;
//			    boolean newBlock;

		  } else if (editOp instanceof AddBlockOp) {
			  AddBlockOp op = (AddBlockOp) editOp;
			  // private String path;
			  // private Block penultimateBlock;
			  // private Block lastBlock;
			  String path = op.getPath();
			  CompositeFsPathChange chg = pathChangeOrCreate(path);
			  chg.addBlockOp(op);
			  handleChg = true;
		  } else if (editOp instanceof UpdateBlocksOp) {
		  } else if (editOp instanceof SetReplicationOp) {
		  } else if (editOp instanceof ConcatDeleteOp) {
		  } else if (editOp instanceof RenameOldOp) {
		  } else if (editOp instanceof DeleteOp) {
			  DeleteOp op = (DeleteOp) editOp;
			  String path = op.path;
			  CompositeFsPathChange prevChg = removeFileChange(path);
			  boolean wasNew = false;
			  if (prevChg != null) {
				  if (!prevChg.ops.isEmpty()) {
					  val firstChg = prevChg.ops.get(0);
					  wasNew = firstChg.getClass().equals(AddFileFsChange.class)
							  || firstChg.getClass().equals(MkdirFsChange.class)
							  ;
				  }
			  }
			  if (wasNew) {
				  // temporary file created then deleted .. do nothing (modify parent dir modification time?)!
			  } else {
				  String prevPath = prevChg != null? prevChg.getOriginPath() : null;
				  pathDeletedChg.put(path, new DeleteFileFsChange(prevPath, path));
			  }
			  handleChg = true;

		  } else if (editOp instanceof MkdirOp) {
			  MkdirOp op = (MkdirOp) editOp;
			  String path = op.path;
			  CompositeFsPathChange chg = pathChangeOrCreate(path);
			  chg.addMkdirOp(op);
			  handleChg = true;
		  } else if (editOp instanceof SetGenstampV1Op) {
			  // long genStampV1;
			  handleChg = true;
		  } else if (editOp instanceof SetGenstampV2Op) {
			  // long genStampV2;
			  handleChg = true;
		  } else if (editOp instanceof AllocateBlockIdOp) {
			  // long blockId;
			  handleChg = true;
		  } else if (editOp instanceof SetPermissionsOp) {
			  SetPermissionsOp op = (SetPermissionsOp) editOp;
			  String path = op.src;
			  CompositeFsPathChange chg = pathChangeOrCreate(path);
			  chg.addSetPermissionOp(op);
			  handleChg = true;
			  
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
			  handleChg = true;
		  } else if (editOp instanceof EndLogSegmentOp) {
			  handleChg = true;
		  } else if (editOp instanceof InvalidOp) {
			  handleChg = true;
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

		if (usePrintToString && !handleChg) {
			println(editOp.toString());
		}
	}

	private CompositeFsPathChange removeFileChange(String path) {
		AbstractFsChange prevChg = currPathChange.remove(path);
		CompositeFsPathChange chg;
		if (prevChg instanceof CompositeFsPathChange) {
			chg = (CompositeFsPathChange) prevChg;
		} else {
			chg = null;
		}
		return chg;
	}
	
	private CompositeFsPathChange pathChangeOrCreate(String path) {
		AbstractFsChange prevChg = currPathChange.get(path);
		CompositeFsPathChange chg;
		if (prevChg instanceof CompositeFsPathChange) {
			chg = (CompositeFsPathChange) prevChg;
		} else {
			chg = new CompositeFsPathChange(null, path);
		}
		return chg;
	}

	private void println(String text) {
		System.out.println(text);
	}
}
