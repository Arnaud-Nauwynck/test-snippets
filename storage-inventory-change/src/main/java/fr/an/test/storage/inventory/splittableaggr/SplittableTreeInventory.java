package fr.an.test.storage.inventory.splittableaggr;

public class SplittableTreeInventory {

    private static long MEGA = 1024 * 1024;
    private static long GIGA = 1024 * MEGA;

    public final long splitPreferredSize;

    protected final SplittableTreeNodeInventory root;

    //---------------------------------------------------------------------------------------------

    public SplittableTreeInventory(long splitPreferredSize) {
        this.splitPreferredSize = splitPreferredSize;
        this.root = SplittableTreeNodeInventory.createRoot(this);
    }

    //---------------------------------------------------------------------------------------------

}
