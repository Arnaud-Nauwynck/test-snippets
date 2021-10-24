package fr.an.tests.hadoopfsinstrumented.stats;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.statistics.MeanStatistic;
import org.apache.hadoop.fs.statistics.impl.AbstractIOStatisticsImpl;

import lombok.val;

/**
 * IO Statistics for InstrumentedFSInputStream
 */
public class InstrumentedFSInputStreamStats extends AbstractIOStatisticsImpl {

	public final InstrumentedMethodIOBytesStatistics readBytesStats = new InstrumentedMethodIOBytesStatistics();
	
	public final InstrumentedMethodStatistics seekStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodIOBytesStatistics skipBytesStats = new InstrumentedMethodIOBytesStatistics();
	public final InstrumentedMethodStatistics resetStats = new InstrumentedMethodStatistics();
	
	
	
	// implements org.apache.hadoop.fs.statistics.IOStatistics
	// ------------------------------------------------------------------------
	
	@Override
	public Map<String, Long> counters() {
		val res = new HashMap<String, Long>();
		res.put("read_count", (long)readBytesStats.getCount());
		res.put("read_nanos", readBytesStats.getSumNanos());
		res.put("read_sumBytes", readBytesStats.getSumBytes());

		res.put("seek_count", (long)seekStats.getCount());
		res.put("seek_nanos", seekStats.getSumNanos());

		res.put("skip_count", (long)skipBytesStats.getCount());
		res.put("skip_nanos", skipBytesStats.getSumNanos());
		res.put("skip_sumBytes", skipBytesStats.getSumBytes());

		res.put("reset_count", (long)resetStats.getCount());
		res.put("reset_nanos", resetStats.getSumNanos());

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
