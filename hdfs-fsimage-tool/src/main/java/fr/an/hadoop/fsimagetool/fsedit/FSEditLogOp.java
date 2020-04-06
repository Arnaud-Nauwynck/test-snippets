package fr.an.hadoop.fsimagetool.fsedit;

import java.util.List;

import org.apache.hadoop.fs.Options.Rename;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.security.token.delegation.DelegationTokenIdentifier;
import org.apache.hadoop.hdfs.server.namenode.FSEditLogOpCodes;
import org.apache.hadoop.security.token.delegation.DelegationKey;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public abstract class FSEditLogOp {

  long txid;
  byte[] rpcClientId;
  int rpcCallId;

  //??  private boolean useCache = true;

  public abstract FSEditLogOpCodes getOpCode();

  public abstract void accept(FsEditLogOpVisitor visitor);	

  public abstract class FsEditLogOpVisitor {
	 public abstract void visit(AddOp op);
	 public abstract void visit(CloseOp op);
	 public abstract void visit(AppendOp op);
	 public abstract void visit(AddBlockOp op);
	 public abstract void visit(UpdateBlocksOp op);       
	 public abstract void visit(SetReplicationOp op);
	 public abstract void visit(ConcatDeleteOp op);
	 public abstract void visit(RenameOldOp op);
	 public abstract void visit(DeleteOp op);
	 public abstract void visit(MkdirOp op);
	 public abstract void visit(SetGenstampV1Op op);
	 public abstract void visit(SetGenstampV2Op op);
	 public abstract void visit(AllocateBlockIdOp op);
	 public abstract void visit(SetPermissionsOp op);
	 public abstract void visit(SetOwnerOp op);
	 public abstract void visit(SetNSQuotaOp op);
	 public abstract void visit(ClearNSQuotaOp op);
	 public abstract void visit(SetQuotaOp op);
	 public abstract void visit(SetQuotaByStorageTypeOp op);
	 public abstract void visit(TimesOp op);
	 public abstract void visit(SymlinkOp op);
	 public abstract void visit(RenameOp op);
	 public abstract void visit(TruncateOp op);
	 public abstract void visit(ReassignLeaseOp op);
	 public abstract void visit(GetDelegationTokenOp op);       
	 public abstract void visit(RenewDelegationTokenOp op);
	 public abstract void visit(CancelDelegationTokenOp op);
	 public abstract void visit(UpdateMasterKeyOp op); 
	 public abstract void visit(LogSegmentOp op);
	 public abstract void visit(StartLogSegmentOp op);          
	 public abstract void visit(EndLogSegmentOp op);
	 public abstract void visit(InvalidOp op);
	 public abstract void visit(CreateSnapshotOp op);
	 public abstract void visit(DeleteSnapshotOp op);
	 public abstract void visit(RenameSnapshotOp op);
	 public abstract void visit(AllowSnapshotOp op);
	 public abstract void visit(DisallowSnapshotOp op);         
	 public abstract void visit(AddCacheDirectiveInfoOp op);    
	 public abstract void visit(ModifyCacheDirectiveInfoOp op); 
	 public abstract void visit(RemoveCacheDirectiveInfoOp op); 
	 public abstract void visit(AddCachePoolOp op);
	 public abstract void visit(ModifyCachePoolOp op);
	 public abstract void visit(RemoveCachePoolOp op);
	 public abstract void visit(RemoveXAttrOp op);
	 public abstract void visit(SetXAttrOp op);
	 public abstract void visit(SetAclOp op);
	 public abstract void visit(AddErasureCodingPolicyOp op);
	 public abstract void visit(EnableErasureCodingPolicyOp op);
	 public abstract void visit(DisableErasureCodingPolicyOp op);
	 public abstract void visit(RemoveErasureCodingPolicyOp op);
	 public abstract void visit(RollingUpgradeOp op);
	 public abstract void visit(SetStoragePolicyOp op);
	 public abstract void visit(RollingUpgradeStartOp op);
	 public abstract void visit(RollingUpgradeFinalizeOp op);
  }
  
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static abstract class AddCloseOp extends FSEditLogOp {
    int length;
    long inodeId;
    String path;
    short replication;
    long mtime;
    long atime;
    long blockSize;
    Block[] blocks;
    PermissionStatus permissions;
    List<AclEntry> aclEntries;
    List<XAttr> xAttrs;
    String clientName;
    String clientMachine;
    boolean overwrite;
    byte storagePolicyId;
    byte erasureCodingPolicyId;

  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class AddOp extends AddCloseOp {
	    @Override
	    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ADD; }

	    @Override
	    public void accept(FsEditLogOpVisitor visitor) {
	    	visitor.visit(this);
	    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class CloseOp extends AddCloseOp {
	    @Override
	    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_CLOSE; }

	    @Override
	    public void accept(FsEditLogOpVisitor visitor) {
	    	visitor.visit(this);
	    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class AppendOp extends FSEditLogOp {
    String path;
    String clientName;
    String clientMachine;
    boolean newBlock;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_APPEND; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class AddBlockOp extends FSEditLogOp {
    private String path;
    private Block penultimateBlock;
    private Block lastBlock;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ADD_BLOCK; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class UpdateBlocksOp extends FSEditLogOp {
    String path;
    Block[] blocks;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_UPDATE_BLOCKS; }
  
    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetReplicationOp extends FSEditLogOp {
    String path;
    short replication;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_REPLICATION; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class ConcatDeleteOp extends FSEditLogOp {
    int length;
    String trg;
    String[] srcs;
    long timestamp;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_CONCAT_DELETE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class RenameOldOp extends FSEditLogOp {
    int length;
    String src;
    String dst;
    long timestamp;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_RENAME_OLD; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class DeleteOp extends FSEditLogOp {
    int length;
    String path;
    long timestamp;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_DELETE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class MkdirOp extends FSEditLogOp {
    int length;
    long inodeId;
    String path;
    long timestamp;
    PermissionStatus permissions;
    List<AclEntry> aclEntries;
    List<XAttr> xAttrs;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_MKDIR; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetGenstampV1Op extends FSEditLogOp {
    long genStampV1;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_GENSTAMP_V1; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetGenstampV2Op extends FSEditLogOp {
    long genStampV2;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_GENSTAMP_V2; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class AllocateBlockIdOp extends FSEditLogOp {
    long blockId;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ALLOCATE_BLOCK_ID; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetPermissionsOp extends FSEditLogOp {
    String src;
    FsPermission permissions;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_PERMISSIONS; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetOwnerOp extends FSEditLogOp {
    String src;
    String username;
    String groupname;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_OWNER; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetNSQuotaOp extends FSEditLogOp {
    String src;
    long nsQuota;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_NS_QUOTA; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class ClearNSQuotaOp extends FSEditLogOp {
    String src;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_CLEAR_NS_QUOTA; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetQuotaOp extends FSEditLogOp {
    String src;
    long nsQuota;
    long dsQuota;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_QUOTA; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetQuotaByStorageTypeOp extends FSEditLogOp {
    String src;
    long dsQuota;
    StorageType type;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_QUOTA_BY_STORAGETYPE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class TimesOp extends FSEditLogOp {
    int length;
    String path;
    long mtime;
    long atime;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_TIMES; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SymlinkOp extends FSEditLogOp {
    int length;
    long inodeId;
    String path;
    String value;
    long mtime;
    long atime;
    PermissionStatus permissionStatus;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SYMLINK; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class RenameOp extends FSEditLogOp {
    int length;
    String src;
    String dst;
    long timestamp;
    Rename[] options;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_RENAME; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class TruncateOp extends FSEditLogOp {
    String src;
    String clientName;
    String clientMachine;
    long newLength;
    long timestamp;
    Block truncateBlock;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_TRUNCATE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
 
  @Data @EqualsAndHashCode(callSuper=true)
  public static class ReassignLeaseOp extends FSEditLogOp {
    String leaseHolder;
    String path;
    String newHolder;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_REASSIGN_LEASE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class GetDelegationTokenOp extends FSEditLogOp {
    DelegationTokenIdentifier token;
    long expiryTime;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_GET_DELEGATION_TOKEN; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class RenewDelegationTokenOp extends FSEditLogOp {
    DelegationTokenIdentifier token;
    long expiryTime;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_RENEW_DELEGATION_TOKEN; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class CancelDelegationTokenOp extends FSEditLogOp {
    DelegationTokenIdentifier token;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_CANCEL_DELEGATION_TOKEN; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class UpdateMasterKeyOp extends FSEditLogOp {
    DelegationKey key;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_UPDATE_MASTER_KEY; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static abstract class LogSegmentOp extends FSEditLogOp {
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class StartLogSegmentOp extends LogSegmentOp {

	    @Override
	    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_START_LOG_SEGMENT; }

	    @Override
	    public void accept(FsEditLogOpVisitor visitor) {
	    	visitor.visit(this);
	    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class EndLogSegmentOp extends LogSegmentOp {

	    @Override
	    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_END_LOG_SEGMENT; }

	    @Override
	    public void accept(FsEditLogOpVisitor visitor) {
	    	visitor.visit(this);
	    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class InvalidOp extends FSEditLogOp {

	    @Override
	    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_INVALID; }

	    @Override
	    public void accept(FsEditLogOpVisitor visitor) {
	    	visitor.visit(this);
	    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class CreateSnapshotOp extends FSEditLogOp {
    String snapshotRoot;
    String snapshotName;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_CREATE_SNAPSHOT; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class DeleteSnapshotOp extends FSEditLogOp {
    String snapshotRoot;
    String snapshotName;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_DELETE_SNAPSHOT; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class RenameSnapshotOp extends FSEditLogOp {
    String snapshotRoot;
    String snapshotOldName;
    String snapshotNewName;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_RENAME_SNAPSHOT; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class AllowSnapshotOp extends FSEditLogOp { // @Idempotent
    String snapshotRoot;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ALLOW_SNAPSHOT; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class DisallowSnapshotOp extends FSEditLogOp { // @Idempotent
    String snapshotRoot;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_DISALLOW_SNAPSHOT; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class AddCacheDirectiveInfoOp extends FSEditLogOp {
    CacheDirectiveInfo directive;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ADD_CACHE_DIRECTIVE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class ModifyCacheDirectiveInfoOp extends FSEditLogOp {
    CacheDirectiveInfo directive;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_MODIFY_CACHE_DIRECTIVE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class RemoveCacheDirectiveInfoOp extends FSEditLogOp {
    long id;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_REMOVE_CACHE_DIRECTIVE; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class AddCachePoolOp extends FSEditLogOp {
    CachePoolInfo info;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ADD_CACHE_POOL; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class ModifyCachePoolOp extends FSEditLogOp {
    CachePoolInfo info;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_MODIFY_CACHE_POOL; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class RemoveCachePoolOp extends FSEditLogOp {
    String poolName;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_REMOVE_CACHE_POOL; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class RemoveXAttrOp extends FSEditLogOp {
    List<XAttr> xAttrs;
    String src;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_REMOVE_XATTR; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }
  
  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetXAttrOp extends FSEditLogOp {
    List<XAttr> xAttrs;
    String src;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_XATTR; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetAclOp extends FSEditLogOp {
    List<AclEntry> aclEntries = Lists.newArrayList();
    String src;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_ACL; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class AddErasureCodingPolicyOp extends FSEditLogOp {
    private ErasureCodingPolicy ecPolicy;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ADD_ERASURE_CODING_POLICY; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class EnableErasureCodingPolicyOp extends FSEditLogOp {
    private String ecPolicyName;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ENABLE_ERASURE_CODING_POLICY; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  /**
   * Operation corresponding to disable an erasure coding policy.
   */
  @Data @EqualsAndHashCode(callSuper=true)
  public static class DisableErasureCodingPolicyOp extends FSEditLogOp {
    private String ecPolicyName;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_DISABLE_ERASURE_CODING_POLICY; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  /**
   * Operation corresponding to remove an erasure coding policy.
   */
  @Data @EqualsAndHashCode(callSuper=true)
  public static class RemoveErasureCodingPolicyOp extends FSEditLogOp {
    private String ecPolicyName;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_REMOVE_ERASURE_CODING_POLICY; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }

  /**
   * Operation corresponding to upgrade
   */
  @Data @EqualsAndHashCode(callSuper=true)
  abstract public static class RollingUpgradeOp extends FSEditLogOp {
    private String name;
    private long time;
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class SetStoragePolicyOp extends FSEditLogOp {
    String path;
    byte policyId;

    @Override
    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_SET_STORAGE_POLICY; }

    @Override
    public void accept(FsEditLogOpVisitor visitor) {
    	visitor.visit(this);
    }
  }  

  @Data @EqualsAndHashCode(callSuper=true)
  public static class RollingUpgradeStartOp extends RollingUpgradeOp {

	    @Override
	    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ROLLING_UPGRADE_START; }

	    @Override
	    public void accept(FsEditLogOpVisitor visitor) {
	    	visitor.visit(this);
	    }
  }

  @Data @EqualsAndHashCode(callSuper=true)
  public static class RollingUpgradeFinalizeOp extends RollingUpgradeOp {

	    @Override
	    public FSEditLogOpCodes getOpCode() { return FSEditLogOpCodes.OP_ROLLING_UPGRADE_FINALIZE; }

	    @Override
	    public void accept(FsEditLogOpVisitor visitor) {
	    	visitor.visit(this);
	    }
  }

}
