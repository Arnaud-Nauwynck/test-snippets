package fr.an.test.storage.inventory;

import fr.an.test.storage.inventory.TreeNodeInventoryKnowledge.*;

/**
 * Visitor desin-pattern (aka object-oriented switch-case) on TreeNodeInventoryChange AST classes
 */
public abstract class TreeNodeInventoryKnowledgeVisitor {

    public abstract void caseDeleted(NonExistingTreeNodeInventoryKnowledge p);

    public abstract void caseUnknown(UnknownTreeNodeInventoryKnowledge p);

    public abstract void caseFileUnknownLen(FileUnknownLenTreeNodeInventoryKnowledge p);

    public abstract void caseFile(FileWithLenTreeNodeInventoryKnowledge p);

    public abstract void caseDir(DirTreeNodeInventoryKnowledge p);

    //---------------------------------------------------------------------------------------------

    public static class DefaultTreeNodeInventoryKnowledgeVisitor extends TreeNodeInventoryKnowledgeVisitor {

        @Override
        public void caseDeleted(NonExistingTreeNodeInventoryKnowledge p) {}

        @Override
        public void caseUnknown(UnknownTreeNodeInventoryKnowledge p) {}

        @Override
        public void caseFileUnknownLen(FileUnknownLenTreeNodeInventoryKnowledge p) {}

        @Override
        public void caseFile(FileWithLenTreeNodeInventoryKnowledge p) {}

        @Override
        public void caseDir(DirTreeNodeInventoryKnowledge p) {}
    }
}
