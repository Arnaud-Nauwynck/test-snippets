package fr.an.test.storage.inventory;

import fr.an.test.storage.inventory.TreeNodeInventoryStatus.FileUnknownLenTreeNodeInventoryStatus;
import fr.an.test.storage.inventory.TreeNodeInventoryStatus.FileWithLenTreeNodeInventoryStatus;

public abstract class TreeNodeInventoryStatusVisitor {
    public abstract void caseDeleted(TreeNodeInventoryStatus.NonExistingTreeNodeInventoryStatus node);

    public abstract void caseUnknown(TreeNodeInventoryStatus.UnknownTreeNodeInventoryStatus node);

    public abstract void caseFileUnknownLen(FileUnknownLenTreeNodeInventoryStatus node);
    public abstract void caseFile(FileWithLenTreeNodeInventoryStatus node);

    public abstract void caseDir(TreeNodeInventoryStatus.DirTreeNodeInventoryStatus node);

}
