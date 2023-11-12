package fr.an.test.storage.inventory;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.TreeMap;

public abstract class TreeNodeInventoryStatus {

    public abstract void visit(TreeNodeInventoryStatusVisitor visitor);

    public static NonExistingTreeNodeInventoryStatus ofNonExisting() {
        return NonExistingTreeNodeInventoryStatus.INSTANCE;
    }
    public static UnknownTreeNodeInventoryStatus ofUnknown() {
        return UnknownTreeNodeInventoryStatus.INSTANCE;
    }
    public static FileUnknownLenTreeNodeInventoryStatus ofFileUnknownLen() {
        return FileUnknownLenTreeNodeInventoryStatus.INSTANCE;
    }
    public static FileWithLenTreeNodeInventoryStatus ofFile(long len) {
        return new FileWithLenTreeNodeInventoryStatus(len);
    }
    public static DirTreeNodeInventoryStatus ofDir(ImmutableMap<String, TreeNodeInventoryStatus> child, boolean childMapComplete) {
        return new DirTreeNodeInventoryStatus(child, childMapComplete);
    }

    //---------------------------------------------------------------------------------------------

    public static class NonExistingTreeNodeInventoryStatus extends TreeNodeInventoryStatus {
        public static final NonExistingTreeNodeInventoryStatus INSTANCE = new NonExistingTreeNodeInventoryStatus();

        private NonExistingTreeNodeInventoryStatus() {
        }

        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseDeleted(this);
        }

    }

    public static class UnknownTreeNodeInventoryStatus extends TreeNodeInventoryStatus {
        public static final UnknownTreeNodeInventoryStatus INSTANCE = new UnknownTreeNodeInventoryStatus();

        private UnknownTreeNodeInventoryStatus() {
        }

        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseUnknown(this);
        }

    }

    public static abstract class AbstractFileTreeNodeInventoryStatus extends TreeNodeInventoryStatus {
    }

    public static class FileUnknownLenTreeNodeInventoryStatus extends AbstractFileTreeNodeInventoryStatus {
        public static final FileUnknownLenTreeNodeInventoryStatus INSTANCE = new FileUnknownLenTreeNodeInventoryStatus();

        private FileUnknownLenTreeNodeInventoryStatus() {
        }

        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseFileUnknownLen(this);
        }

    }

    public static class FileWithLenTreeNodeInventoryStatus extends AbstractFileTreeNodeInventoryStatus {
        public final long fileLen;
        public FileWithLenTreeNodeInventoryStatus(long fileLen) {
            this.fileLen = fileLen;
        }
        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseFile(this);
        }

    }


    public static class DirTreeNodeInventoryStatus extends TreeNodeInventoryStatus {
        public final ImmutableMap<String,TreeNodeInventoryStatus> child;
        public final boolean childMapComplete;

        public DirTreeNodeInventoryStatus(ImmutableMap<String, TreeNodeInventoryStatus> child, boolean childMapComplete) {
            this.child = child;
            this.childMapComplete = childMapComplete;
        }

        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseDir(this);
        }

    }

}
