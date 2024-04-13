package fr.an.test.storage.inventory;

import fr.an.test.storage.inventory.TreeNodeInventoryKnowledge.*;

import java.util.Map;

public abstract class TreeNodeInventoryChange {

    public abstract TreeNodeInventoryKnowledge getPrevKnowledge();

    public abstract TreeNodeInventoryKnowledge getNextKnowledge();

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
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return NonExistingTreeNodeInventoryKnowledge.INSTANCE;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
            return new FileWithLenTreeNodeInventoryKnowledge(fileLen);
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseFileCreated(this);
        }
    }

    /**
     * None -> File(? unknown len)
     */
    public static class FileUnknownLenCreatedTreeNodeInventoryChange extends TreeNodeInventoryChange {

        @Override
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return NonExistingTreeNodeInventoryKnowledge.INSTANCE;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
            return FileUnknownLenTreeNodeInventoryKnowledge.INSTANCE;
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
        public AbstractFileTreeNodeInventoryStatus getPrevKnowledge() {
            return new FileWithLenTreeNodeInventoryKnowledge(prevFileLen);
        }

        @Override
        public AbstractFileTreeNodeInventoryStatus getNextKnowledge() {
            return new FileWithLenTreeNodeInventoryKnowledge(nextFileLen);
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
        public AbstractFileTreeNodeInventoryStatus getPrevKnowledge() {
            return FileUnknownLenTreeNodeInventoryKnowledge.INSTANCE;
        }

        @Override
        public AbstractFileTreeNodeInventoryStatus getNextKnowledge() {
            return new FileWithLenTreeNodeInventoryKnowledge(nextFileLen);
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
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return new FileWithLenTreeNodeInventoryKnowledge(prevFileLen);
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
            return NonExistingTreeNodeInventoryKnowledge.INSTANCE;
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
        public final DirTreeNodeInventoryKnowledge nextStatus;

        public DirCreatedTreeNodeInventoryChange(DirTreeNodeInventoryKnowledge nextStatus) {
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return NonExistingTreeNodeInventoryKnowledge.INSTANCE;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
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
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            throw NotImpl.throwNotImpl(); // TODO
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
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
        public final DirTreeNodeInventoryKnowledge prevStatus;

        public DirDeletedTreeNodeInventoryChange(DirTreeNodeInventoryKnowledge prevStatus) {
            this.prevStatus = prevStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return prevStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
            return NonExistingTreeNodeInventoryKnowledge.INSTANCE;
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
        public final DirTreeNodeInventoryKnowledge nextStatus;

        public MutatedFileToDirTreeNodeInventoryChange(AbstractFileTreeNodeInventoryStatus prevStatus, DirTreeNodeInventoryKnowledge nextStatus) {
            this.prevStatus = prevStatus;
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return prevStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
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
        public final DirTreeNodeInventoryKnowledge prevStatus;
        public final AbstractFileTreeNodeInventoryStatus nextStatus;

        public MutatedDirToFileTreeNodeInventoryChange(DirTreeNodeInventoryKnowledge prevStatus, AbstractFileTreeNodeInventoryStatus nextStatus) {
            this.prevStatus = prevStatus;
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return prevStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
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
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return UnknownTreeNodeInventoryKnowledge.INSTANCE;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
            return NonExistingTreeNodeInventoryKnowledge.INSTANCE;
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
        public final DirTreeNodeInventoryKnowledge nextStatus;

        public MutatedUnknownToDirTreeNodeInventoryChange(DirTreeNodeInventoryKnowledge nextStatus) {
            this.nextStatus = nextStatus;
        }

        @Override
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return UnknownTreeNodeInventoryKnowledge.INSTANCE;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
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
        public TreeNodeInventoryKnowledge getPrevKnowledge() {
            return UnknownTreeNodeInventoryKnowledge.INSTANCE;
        }

        @Override
        public TreeNodeInventoryKnowledge getNextKnowledge() {
            return nextStatus;
        }

        @Override
        public void visit(TreeNodeInventoryChangeVisitor visitor) {
            visitor.caseMutatedUnknownToFile(this);
        }
    }

}
