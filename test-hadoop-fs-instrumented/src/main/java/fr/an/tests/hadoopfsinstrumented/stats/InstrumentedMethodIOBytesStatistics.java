package fr.an.tests.hadoopfsinstrumented.stats;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public final class InstrumentedMethodIOBytesStatistics { //  extends AbstractIOStatisticsImpl

	public static final String METRIC_COUNT = "count";
//	public static final String METRIC_MINIMUM_NANOS = "min_nanos";
//	public static final String METRIC_MAXIMUM_NANOS = "max_nanos";
	public static final String METRIC_SUM_NANOS = "sum_nanos";
	public static final String METRIC_SUM_BYTES = "sum_bytes";
//	public static final String METRIC_MIN_NANOS_PER_BYTES = "min_nanos_per_byte";
//	public static final String METRIC_MAX_NANOS_PER_BYTES = "max_nanos_per_byte";

    // setup to use Unsafe.compareAndSwapLong for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long countOffset;
    private static final long sumNanosOffset;
    private static final long sumBytesOffset;

    static {
        try {
        	countOffset = unsafe.objectFieldOffset(InstrumentedMethodIOBytesStatistics.class.getDeclaredField("count"));
        	sumNanosOffset = unsafe.objectFieldOffset(InstrumentedMethodIOBytesStatistics.class.getDeclaredField("sumNanos"));
        	sumBytesOffset = unsafe.objectFieldOffset(InstrumentedMethodIOBytesStatistics.class.getDeclaredField("sumBytes"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int count;
    private volatile long sumNanos;
    private volatile long sumBytes;
    
	public void increment(long nanos, long bytes) {
		// count++;
		unsafe.getAndAddInt(this, countOffset, 1);
		// sumNanos += nanos;
		unsafe.getAndAddLong(this, sumNanosOffset, nanos);
		// sumBytes++;
		unsafe.getAndAddLong(this, sumBytesOffset, bytes);
	}
	
	
	public int getCount() {
		return count;
	}

	public long getSumNanos() {
		return sumNanos;
	}

	public long getSumBytes() {
		return sumBytes;
	}

//	@AllArgsConstructor
//	public static class StatSnapshot {
//		public final int count;
//		public final long sumNanos;
//		public final long sumBytes;
//	}
	
}
