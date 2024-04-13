package fr.an.test.storage.inventory;

import fr.an.test.storage.inventory.TreeNodeInventoryChange.*;

/**
 * Visitor design-pattern (aka object-oriented switch-case) on TreeNodeInventoryChange AST classes
 */
public abstract class TreeNodeInventoryChangeVisitor {

    public abstract void caseFileCreated(FileCreatedTreeNodeInventoryChange change);

    public abstract void caseFileUnknownLenCreated(FileUnknownLenCreatedTreeNodeInventoryChange change);

    public abstract void caseFileModified(FileModifiedTreeNodeInventoryChange change);

    public abstract void caseUnknownFileToFileModified(UnknownFileToFileWithLenModifiedTreeNodeInventoryChange change);

    public abstract void caseFileDeleted(FileDeletedTreeNodeInventoryChange change);

    public abstract void caseDirCreated(DirCreatedTreeNodeInventoryChange change);

    public abstract void caseDirModified(DirModifiedTreeNodeInventoryChange change);

    public abstract void caseDirDeleted(DirDeletedTreeNodeInventoryChange change);

    public abstract void caseMutatedFileToDir(MutatedFileToDirTreeNodeInventoryChange change);

    public abstract void caseMutatedDirToFile(MutatedDirToFileTreeNodeInventoryChange change);

    public abstract void caseMutatedUnknownToDir(MutatedUnknownToDirTreeNodeInventoryChange change);

    public abstract void caseMutatedUnknownToFile(MutatedUnknownToFileTreeNodeInventoryChange change);

    public abstract void caseUnknownDeleted(UnknownDeletedTreeNodeInventoryChange change);

}
