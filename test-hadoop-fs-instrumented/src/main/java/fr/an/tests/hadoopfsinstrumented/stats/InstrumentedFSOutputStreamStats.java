package fr.an.tests.hadoopfsinstrumented.stats;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.statistics.MeanStatistic;
import org.apache.hadoop.fs.statistics.impl.AbstractIOStatisticsImpl;

import lombok.val;

/**
 * IO Statistics for InstrumentedFSInputStream
 */
public class InstrumentedFSOutputStreamStats extends AbstractIOStatisticsImpl {

	public final InstrumentedMethodIOBytesStatistics writeBytesStats = new InstrumentedMethodIOBytesStatistics();

	public final InstrumentedMethodStatistics hflushStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics hsyncStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics flushStats = new InstrumentedMethodStatistics();
	
	
	
	// implements org.apache.hadoop.fs.statistics.IOStatistics
	// ------------------------------------------------------------------------
	
	@Override
	public Map<String, Long> counters() {
		val res = new HashMap<String, Long>();
		res.put("write_count", (long)writeBytesStats.getCount());
		res.put("write_nanos", writeBytesStats.getSumNanos());
		res.put("write_sumBytes", writeBytesStats.getSumBytes());

		res.put("hflush_count", (long)hflushStats.getCount());
		res.put("hflush_nanos", hflushStats.getSumNanos());

		res.put("hsync_count", (long)hsyncStats.getCount());
		res.put("hsync_nanos", hsyncStats.getSumNanos());

		res.put("flush_count", (long)flushStats.getCount());
		res.put("flush_nanos", flushStats.getSumNanos());

		return res;
	}

	@Override
	public Map<String, Long> gauges() {
		val res = new HashMap<String, Long>();
		// nop
		return res;
	}

	@Override
	public Map<String, Long> minimums() {
		val res = new HashMap<String, Long>();
		// nop
		return res;
	}

	@Override
	public Map<String, Long> maximums() {
		val res = new HashMap<String, Long>();
		// nop
		return res;
	}

	@Override
	public Map<String, MeanStatistic> meanStatistics() {
		val res = new HashMap<String, MeanStatistic>();
		// nop
		return res;
	}
	
}
