package fr.an.test.storage.inventory.splittableaggr;

import lombok.Getter;
import lombok.val;

import java.util.Map;
import java.util.TreeMap;

/**
 * hierarchical node for storage inventory,
 * splittable to synthesize info in whole subtrees instead of having billions of nodes
 */
public class SplittableTreeNodeInventory {

    protected static final int MASK_NEED_SPLIT = 0x1;
    protected static final int MASK_FORCE_SPLIT = 0x2;
    protected static final int MASK_NEED_MERGE = 0x3;
    protected static final int MASK_NEED_RESCAN = 0x4;

    protected final SplittableTreeInventory tree; // could be implicit, cf parent.parent(^N) up to RootSplittableTreeNodeInventory.tree
    public final SplittableTreeNodeInventory parent;
    public final String name;

    protected Map<String, SplittableTreeNodeInventory> splitChildMap; // = new TreeMap<>();

    protected int flags;

    protected long mergeFileCount;
    protected long mergeFileTotalSize;
    protected long mergeDirCount;

    protected long mergeUnknownCount;

    protected long splitTime;
    protected long lastMergeItemModTime;

    protected long lastRescanTime;

    /**
     * cached of computation recursively on splitChild
     * when modifying any field, the cache computations should be cleared up to root ancestors
     */
    protected SplittableTreeInventorySynthetizedAttrs cachedSynthetizedAttrs;

    protected static class RootSplittableTreeNodeInventory extends SplittableTreeNodeInventory {
        // public final SplittableTreeInventory tree; // redundant, but could be stored only here

        public RootSplittableTreeNodeInventory(SplittableTreeInventory tree) {
            super(tree, null, "");
            // this.tree = tree;
        }
    }

    //---------------------------------------------------------------------------------------------

    protected SplittableTreeNodeInventory(SplittableTreeInventory tree, SplittableTreeNodeInventory parent, String name) {
        this.tree = tree;
        this.parent = parent;
        this.name = name;
    }

    /*pp*/ static SplittableTreeNodeInventory createRoot(SplittableTreeInventory tree) {
        return new RootSplittableTreeNodeInventory(tree);
    }

    //---------------------------------------------------------------------------------------------

    public boolean isNeedSplitChild() { return 0 != (flags & MASK_NEED_SPLIT); }
    public void setNeedSplitChild(boolean p) { _setFlagMask(p, MASK_NEED_SPLIT); }
    public boolean isForceSplitChild() { return 0 != (flags & MASK_FORCE_SPLIT); }
    public void setForceSplitChild(boolean p) { _setFlagMask(p, MASK_FORCE_SPLIT); }
    public boolean isNeedMerge() { return 0 != (flags & MASK_NEED_MERGE); }
    public void setNeedMerge(boolean p) { _setFlagMask(p, MASK_NEED_MERGE); }
    public boolean isNeedRescan() { return 0 != (flags & MASK_NEED_RESCAN); }
    public void setNeedRescan(boolean p) { _setFlagMask(p, MASK_NEED_RESCAN); }

    protected void _setFlagMask(boolean p, int mask) {
        if (p) {
            this.flags |= mask;
        } else {
            this.flags &= ~mask;
        }
        clearAncestorCachedSynthetizedAttrs();
    }

    public SplittableTreeNodeInventory getOrCreateChild(String name) {
        if (splitChildMap == null) {
            splitChildMap = new TreeMap<>();
        }
        SplittableTreeNodeInventory res = splitChildMap.get(name);
        if (res == null) {
            res = new SplittableTreeNodeInventory(tree, this, name);
            splitChildMap.put(name, res);
            clearAncestorCachedSynthetizedAttrs();
        }
        return res;
    }

    // synthetic attrs
    //---------------------------------------------------------------------------------------------

    protected void clearAncestorCachedSynthetizedAttrs() {
        for(SplittableTreeNodeInventory p = this; p != null; p = p.parent) {
            if (p.cachedSynthetizedAttrs == null) {
                break; // ok, no need to continue on parents, should all be null
            }
            p.cachedSynthetizedAttrs = null;
        }
    }

    public SplittableTreeInventorySynthetizedAttrs recursiveSynthetizedAttrs() {
        if (cachedSynthetizedAttrs != null) {
            return cachedSynthetizedAttrs;
        }
        val res = new SplittableTreeInventorySynthetizedAttrs();
        if (splitChildMap != null && !splitChildMap.isEmpty()) {
            for(val child: splitChildMap.values()) {
                val childAttrs = child.recursiveSynthetizedAttrs();
                res.add(childAttrs);
            }
        }
        res.addMergedPart(this);
        this.cachedSynthetizedAttrs = res;
        return res;
    }

    @Getter
    public static class SplittableTreeInventorySynthetizedAttrs {

        protected long fileCount;
        protected long fileTotalSize;
        protected long dirCount;
        protected long unknownCount;

        protected long lastSplitTime;
        protected long lastItemModTime;
        protected long lastRescanTime;

        protected int unknownSplittableCount;
        protected int needSplitChildCount;
        protected int forceSplitChildCount;
        protected int needMergeCount;
        protected int needRescanCount;

        private void add(SplittableTreeInventorySynthetizedAttrs p) {
            this.fileCount += p.fileCount;
            this.fileTotalSize += p.fileTotalSize;
            this.dirCount += p.dirCount;
            this.unknownCount += p.unknownCount;
            this.lastSplitTime = Math.max(lastSplitTime, p.lastSplitTime);
            this.lastItemModTime = Math.max(lastItemModTime, p.lastItemModTime);
            this.lastRescanTime = Math.max(lastRescanTime, p.lastRescanTime);
            this.unknownSplittableCount += p.unknownSplittableCount;
            this.needSplitChildCount += p.needSplitChildCount;
            this.forceSplitChildCount += p.forceSplitChildCount;
            this.needMergeCount += p.needMergeCount;
            this.needRescanCount += p.needRescanCount;
        }

        private void addMergedPart(SplittableTreeNodeInventory p) {
            this.fileCount += p.mergeFileCount;
            this.fileTotalSize += p.mergeFileTotalSize;
            this.dirCount += p.mergeDirCount;
            this.unknownCount += p.mergeUnknownCount;
            this.unknownSplittableCount += (p.mergeUnknownCount!=0)? 1 : 0;
            this.lastSplitTime = Math.max(lastSplitTime, p.splitTime);
            this.lastItemModTime = Math.max(lastItemModTime, p.lastMergeItemModTime);
            this.lastRescanTime = Math.max(lastRescanTime, p.lastRescanTime);
            this.needSplitChildCount += (p.isNeedSplitChild())? 1 : 0;;
            this.forceSplitChildCount += (p.isForceSplitChild())? 1 : 0;;
            this.needMergeCount += (p.isNeedMerge())? 1 : 0;
            this.needRescanCount += (p.isNeedRescan())? 1 : 0;
        }
    }

    //---------------------------------------------------------------------------------------------


}
