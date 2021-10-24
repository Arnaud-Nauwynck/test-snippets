package fr.an.tests.hadoopfsinstrumented.stats;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class InstrumentedMethodStatistics { //  extends AbstractIOStatisticsImpl

	public static final String METRIC_COUNT = "count";
//	public static final String METRIC_MINIMUM_NANOS = "min_nanos";
//	public static final String METRIC_MAXIMUM_NANOS = "max_nanos";
	public static final String METRIC_SUM_NANOS = "sum_nanos";

    // setup to use Unsafe.compareAndSwapLong for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long countOffset;
    private static final long sumNanosOffset;

    static {
        try {
        	countOffset = unsafe.objectFieldOffset(InstrumentedMethodStatistics.class.getDeclaredField("count"));
        	sumNanosOffset = unsafe.objectFieldOffset(InstrumentedMethodStatistics.class.getDeclaredField("sumNanos"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int count;
    private volatile long sumNanos;
    
	public void increment(long nanos) {
		// count++;
		unsafe.getAndAddInt(this, countOffset, 1);
		// sumNanos += nanos;
		unsafe.getAndAddLong(this, sumNanosOffset, nanos);
	}
	
	
	public int getCount() {
		return count;
	}

	public long getSumNanos() {
		return sumNanos;
	}

//	@AllArgsConstructor
//	public static class StatSnapshot {
//		public final int count;
//		public final long sumNanos;
//	}
	
}
