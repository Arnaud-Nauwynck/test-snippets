package fr.an.test.storage.inventory;

import com.google.common.collect.ImmutableMap;

public abstract class TreeNodeInventoryKnowledge {

    public abstract void visit(TreeNodeInventoryKnowledgeVisitor visitor);

    public static NonExistingTreeNodeInventoryKnowledge ofNonExisting() {
        return NonExistingTreeNodeInventoryKnowledge.INSTANCE;
    }
    public static UnknownTreeNodeInventoryKnowledge ofUnknown() {
        return UnknownTreeNodeInventoryKnowledge.INSTANCE;
    }
    public static FileUnknownLenTreeNodeInventoryKnowledge ofFileUnknownLen() {
        return FileUnknownLenTreeNodeInventoryKnowledge.INSTANCE;
    }
    public static FileWithLenTreeNodeInventoryKnowledge ofFile(long len) {
        return new FileWithLenTreeNodeInventoryKnowledge(len);
    }
    public static DirTreeNodeInventoryKnowledge ofDir(ImmutableMap<String, TreeNodeInventoryKnowledge> child, boolean childMapComplete) {
        return new DirTreeNodeInventoryKnowledge(child, childMapComplete);
    }

    //---------------------------------------------------------------------------------------------

    public static class NonExistingTreeNodeInventoryKnowledge extends TreeNodeInventoryKnowledge {
        public static final NonExistingTreeNodeInventoryKnowledge INSTANCE = new NonExistingTreeNodeInventoryKnowledge();

        private NonExistingTreeNodeInventoryKnowledge() {
        }

        @Override
        public void visit(TreeNodeInventoryKnowledgeVisitor visitor) {
            visitor.caseDeleted(this);
        }

    }

    public static class UnknownTreeNodeInventoryKnowledge extends TreeNodeInventoryKnowledge {
        public static final UnknownTreeNodeInventoryKnowledge INSTANCE = new UnknownTreeNodeInventoryKnowledge();

        private UnknownTreeNodeInventoryKnowledge() {
        }

        @Override
        public void visit(TreeNodeInventoryKnowledgeVisitor visitor) {
            visitor.caseUnknown(this);
        }

    }

    public static abstract class AbstractFileTreeNodeInventoryStatus extends TreeNodeInventoryKnowledge {
    }

    public static class FileUnknownLenTreeNodeInventoryKnowledge extends AbstractFileTreeNodeInventoryStatus {
        public static final FileUnknownLenTreeNodeInventoryKnowledge INSTANCE = new FileUnknownLenTreeNodeInventoryKnowledge();

        private FileUnknownLenTreeNodeInventoryKnowledge() {
        }

        @Override
        public void visit(TreeNodeInventoryKnowledgeVisitor visitor) {
            visitor.caseFileUnknownLen(this);
        }

    }

    public static class FileWithLenTreeNodeInventoryKnowledge extends AbstractFileTreeNodeInventoryStatus {
        public final long fileLen;
        public FileWithLenTreeNodeInventoryKnowledge(long fileLen) {
            this.fileLen = fileLen;
        }
        @Override
        public void visit(TreeNodeInventoryKnowledgeVisitor visitor) {
            visitor.caseFile(this);
        }

    }


    public static class DirTreeNodeInventoryKnowledge extends TreeNodeInventoryKnowledge {
        public final ImmutableMap<String, TreeNodeInventoryKnowledge> child;
        public final boolean childMapComplete;

        public DirTreeNodeInventoryKnowledge(ImmutableMap<String, TreeNodeInventoryKnowledge> child, boolean childMapComplete) {
            this.child = child;
            this.childMapComplete = childMapComplete;
        }

        @Override
        public void visit(TreeNodeInventoryKnowledgeVisitor visitor) {
            visitor.caseDir(this);
        }

    }

}
