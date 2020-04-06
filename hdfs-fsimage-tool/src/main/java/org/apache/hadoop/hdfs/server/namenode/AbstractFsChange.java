package org.apache.hadoop.hdfs.server.namenode;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AddBlockOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.AddOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.CloseOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.MkdirOp;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOp.SetPermissionsOp;

import lombok.Getter;

public abstract class AbstractFsChange {

	@Getter
	private final String originPath;

	public AbstractFsChange(String originPath) {
		super();
		this.originPath = originPath;
	}
	
	
	
	public static class AbstractFsPathChange extends AbstractFsChange {
		private final String path;

		public AbstractFsPathChange(String originPath, String path) {
			super(originPath);
			this.path = path;
		}
		
	}

	
	public static abstract class PathFsChange {
		public final long txId;

		public PathFsChange(long txId) {
			super();
			this.txId = txId;
		}
		
		public abstract String summaryString();
		
	}
	
	public static class CompositeFsPathChange extends AbstractFsPathChange {
		List<PathFsChange> ops = new ArrayList<>();
		
		public CompositeFsPathChange(String originPath, String path) {
			super(originPath, path);
		}

		public void addOp(AddOp op) {
			ops.add(new AddFileFsChange(op.getTransactionId()));
		}
		
		public void addBlockOp(AddBlockOp op) {
			ops.add(new AddBlockFileFsChange(op.getTransactionId(), 
					op.getPenultimateBlock(), op.getLastBlock()));
		}

		public void addCloseOp(CloseOp op) {
			ops.add(new CloseFileFsChange(op.getTransactionId()));
		}

		public void addSetPermissionOp(SetPermissionsOp op) {
			ops.add(new SetPermissionPathFsChange(op.getTransactionId(), op.permissions));
		}

		public void addMkdirOp(MkdirOp op) {
			ops.add(new MkdirFsChange(op));
		}
	}

	
	public static class MkdirFsChange extends PathFsChange {
//	    int length;
//	    long inodeId;
//	    long timestamp;
//	    PermissionStatus permissions;
//	    List<AclEntry> aclEntries;
//	    List<XAttr> xAttrs;

		public MkdirFsChange(MkdirOp op) {
			super(op.getTransactionId());
		}
		
		@Override
		public String summaryString() {
			return "Mkdir";
		}

	}
	
	
	
	public static class AddFileFsChange extends PathFsChange {

		public AddFileFsChange(long txId) {
			super(txId);
		}

		@Override
		public String summaryString() {
			return "Add";
		}

	}
	
	public static class AddBlockFileFsChange extends PathFsChange {
		public final Block penultimateBlock;
		public final Block lastBlock;
		
		public AddBlockFileFsChange(long txId, Block penultimateBlock, Block lastBlock) {
			super(txId);
			this.penultimateBlock = penultimateBlock;
			this.lastBlock = lastBlock;
		}

		@Override
		public String summaryString() {
			return "AddBlock";
		}

	}

	public static class SetPermissionPathFsChange extends PathFsChange {
	    public final FsPermission permissions;

		public SetPermissionPathFsChange(long txId, FsPermission permissions) {
			super(txId);
			this.permissions = permissions;
		}
		
		@Override
		public String summaryString() {
			return "SetPerms";
		}
	}


	public static class CloseFileFsChange extends PathFsChange {

		public CloseFileFsChange(long txId) {
			super(txId);
		}
		
		@Override
		public String summaryString() {
			return "Close";
		}

	}


	public static class DeleteFileFsChange extends AbstractFsPathChange {

		public DeleteFileFsChange(String originPath, String path) {
			super(originPath, path);
		}
		
	}
	
}
