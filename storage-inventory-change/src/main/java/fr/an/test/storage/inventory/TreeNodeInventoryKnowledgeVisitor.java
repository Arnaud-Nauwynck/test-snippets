package fr.an.test.storage.inventory;

import fr.an.test.storage.inventory.TreeNodeInventoryStatus.*;

public abstract class TreeNodeInventoryStatusVisitor {

    public abstract void caseDeleted(NonExistingTreeNodeInventoryStatus p);

    public abstract void caseUnknown(UnknownTreeNodeInventoryStatus p);

    public abstract void caseFileUnknownLen(FileUnknownLenTreeNodeInventoryStatus p);

    public abstract void caseFile(FileWithLenTreeNodeInventoryStatus p);

    public abstract void caseDir(DirTreeNodeInventoryStatus p);

}
