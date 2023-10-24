package fr.an.test.storage.inventory;

import java.util.Map;
import java.util.TreeMap;

public abstract class TreeNodeInventoryStatus {

    public abstract void visit(TreeNodeInventoryStatusVisitor visitor);

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

        public FileUnknownLenTreeNodeInventoryStatus() {
        }

        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseFileUnknownLen(this);
        }

    }

    public static class FileWithLenTreeNodeInventoryStatus extends AbstractFileTreeNodeInventoryStatus {
        protected long fileLen;
        public FileWithLenTreeNodeInventoryStatus(long fileLen) {
            this.fileLen = fileLen;
        }
        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseFile(this);
        }

    }


    public static class DirTreeNodeInventoryStatus extends TreeNodeInventoryStatus {
        protected Map<String,TreeNodeInventoryStatus> child = new TreeMap<>();
        protected boolean childMapComplete;

        @Override
        public void visit(TreeNodeInventoryStatusVisitor visitor) {
            visitor.caseDir(this);
        }

    }

}
