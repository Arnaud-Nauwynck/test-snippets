package fr.an.test.storage.inventory;

import fr.an.test.storage.inventory.TreeNodeInventoryStatus.*;

import java.util.Map;

public abstract class TreeNodeInventoryChange {

    public abstract TreeNodeInventoryStatus getPrevStatus();
    public abstract TreeNodeInventoryStatus getNextStatus();

    public abstract void visit(TreeNodeInventoryChangeVisitor visitor);

    //---------------------------------------------------------------------------------------------

    /**
     * None -> File(fileLen)
     */
    public static class FileCreatedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final long fileLen;

        public FileCreatedTreeNodeInventoryChange(long fileLen) {
            this.fileLen = fileLen;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return NonExistingTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return new FileWithLenTreeNodeInventoryStatus(fileLen);
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseFileCreated(this);
        }
    }

    /**
     * None -> File(?)
     */
    public static class FileUnknownLenCreatedTreeNodeInventoryChange extends TreeNodeInventoryChange {

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return NonExistingTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return FileUnknownLenTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseFileUnknownLenCreated(this);
        }
    }

    /**
     * File(prevFileLen) -> File(nextFileLen)
     */
    public static class FileModifiedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final long prevFileLen;
        public final long nextFileLen;

        public FileModifiedTreeNodeInventoryChange(long prevFileLen, long nextFileLen) {
            this.prevFileLen = prevFileLen;
            this.nextFileLen = nextFileLen;
        }

        @Override
        public AbstractFileTreeNodeInventoryStatus getPrevStatus() {
            return new FileWithLenTreeNodeInventoryStatus(prevFileLen);
        }

        @Override
        public AbstractFileTreeNodeInventoryStatus getNextStatus() {
            return new FileWithLenTreeNodeInventoryStatus(nextFileLen);
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseFileModified(this);
        }
    }

    /**
     * File(?) -> File(fileLen)
     */
    public static class UnknownFileToFileWithLenModifiedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final long nextFileLen;

        public UnknownFileToFileWithLenModifiedTreeNodeInventoryChange(long nextFileLen) {
            this.nextFileLen = nextFileLen;
        }

        @Override
        public AbstractFileTreeNodeInventoryStatus getPrevStatus() {
            return FileUnknownLenTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public AbstractFileTreeNodeInventoryStatus getNextStatus() {
            return new FileWithLenTreeNodeInventoryStatus(nextFileLen);
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseUnknownFileToFileModified(this);
        }
    }

    /**
     * File(prevFileLen) -> None
     */
    public static class FileDeletedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final long prevFileLen;

        public FileDeletedTreeNodeInventoryChange(long prevFileLen) {
            this.prevFileLen = prevFileLen;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return new FileWithLenTreeNodeInventoryStatus(prevFileLen);
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return NonExistingTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseFileDeleted(this);
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * None -> Dir
     */
    public static class DirCreatedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final DirTreeNodeInventoryStatus nextStatus;

        public DirCreatedTreeNodeInventoryChange(DirTreeNodeInventoryStatus nextStatus) {
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return NonExistingTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return nextStatus;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseDirCreated(this);
        }
    }

    /**
     * Dir -> Dir
     */
    public static class DirModifiedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final /*Immutable*/Map<String,TreeNodeInventoryChange> childChange;

        public DirModifiedTreeNodeInventoryChange(Map<String, TreeNodeInventoryChange> childChange) {
            this.childChange = childChange;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            throw NotImpl.throwNotImpl(); // TODO
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            throw NotImpl.throwNotImpl(); // TODO
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseDirModified(this);
        }
    }

    /**
     * Dir -> None
     */
    public static class DirDeletedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final DirTreeNodeInventoryStatus prevStatus;

        public DirDeletedTreeNodeInventoryChange(DirTreeNodeInventoryStatus prevStatus) {
            this.prevStatus = prevStatus;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return prevStatus;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return NonExistingTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseDirDeleted(this);
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * File -> Dir
     */
    public static class MutatedFileToDirTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final AbstractFileTreeNodeInventoryStatus prevStatus;
        public final DirTreeNodeInventoryStatus nextStatus;

        public MutatedFileToDirTreeNodeInventoryChange(AbstractFileTreeNodeInventoryStatus prevStatus, DirTreeNodeInventoryStatus nextStatus) {
            this.prevStatus = prevStatus;
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return prevStatus;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return nextStatus;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseMutatedFileToDir(this);
        }
    }

    /**
     * Dir -> File
     */
    public static abstract class MutatedDirToFileTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final DirTreeNodeInventoryStatus prevStatus;
        public final AbstractFileTreeNodeInventoryStatus nextStatus;

        public MutatedDirToFileTreeNodeInventoryChange(DirTreeNodeInventoryStatus prevStatus, AbstractFileTreeNodeInventoryStatus nextStatus) {
            this.prevStatus = prevStatus;
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return prevStatus;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return nextStatus;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseMutatedDirToFile(this);
        }
    }

    //---------------------------------------------------------------------------------------------

    /**
     * Unknown -> None
     */
    public static class UnknownDeletedTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public static final UnknownDeletedTreeNodeInventoryChange INSTANCE = new UnknownDeletedTreeNodeInventoryChange();

        private UnknownDeletedTreeNodeInventoryChange() {
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return UnknownTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return NonExistingTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseUnknownDeleted(this);
        }
    }

    /**
     * Unknown -> Dir
     */
    public static class MutatedUnknownToDirTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final DirTreeNodeInventoryStatus nextStatus;

        public MutatedUnknownToDirTreeNodeInventoryChange(DirTreeNodeInventoryStatus nextStatus) {
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return UnknownTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return nextStatus;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseMutatedUnknownToDir(this);
        }
    }

    /**
     * Unknown -> File
     */
    public static class MutatedUnknownToFileTreeNodeInventoryChange extends TreeNodeInventoryChange {
        public final AbstractFileTreeNodeInventoryStatus nextStatus;

        public MutatedUnknownToFileTreeNodeInventoryChange(AbstractFileTreeNodeInventoryStatus nextStatus) {
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryStatus getPrevStatus() {
            return UnknownTreeNodeInventoryStatus.INSTANCE;
        }

        @Override
        public TreeNodeInventoryStatus getNextStatus() {
            return nextStatus;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseMutatedUnknownToFile(this);
        }
    }

}
