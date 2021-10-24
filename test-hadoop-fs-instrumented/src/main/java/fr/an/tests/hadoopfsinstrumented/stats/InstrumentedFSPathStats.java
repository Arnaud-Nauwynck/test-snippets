package fr.an.tests.hadoopfsinstrumented.stats;

import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.statistics.MeanStatistic;
import org.apache.hadoop.fs.statistics.impl.AbstractIOStatisticsImpl;

import lombok.val;

/**
 * IO Statistics for InstrumentedFileSystem Path methods
 */
public class InstrumentedFSPathStats extends AbstractIOStatisticsImpl {

	public final InstrumentedMethodStatistics queryFileStatusStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics listStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics listStatusFilterStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics listStatusMultiStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics listStatusFilterMultiStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics globStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics globFilterStats = new InstrumentedMethodStatistics();

	public final InstrumentedMethodStatistics openStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics createStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics createNonRecursiveStats = new InstrumentedMethodStatistics();

	public final InstrumentedMethodStatistics renameStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics mkdirStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics deleteStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics appendStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics concatStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics truncateStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics getStatusStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics listLocatedStatusStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics listStatusIteratorStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics listLocatedFilesStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics getFileChecksumStats = new InstrumentedMethodStatistics();
	public final InstrumentedMethodStatistics getFileChecksumLenStats = new InstrumentedMethodStatistics();

	public final InstrumentedMethodStatistics delegationTokenStats = new InstrumentedMethodStatistics();
	
	// implements org.apache.hadoop.fs.statistics.IOStatistics
	// ------------------------------------------------------------------------
	
	@Override
	public Map<String, Long> counters() {
		val res = new HashMap<String, Long>();
		res.put("queryFileStatus_count", (long)queryFileStatusStats.getCount());
		res.put("queryFileStatus_nanos", queryFileStatusStats.getSumNanos());

		res.put("listStats_count", (long)listStats.getCount());
		res.put("listStats_nanos", listStats.getSumNanos());

		res.put("listStatusFilter_count", (long)listStatusFilterStats.getCount());
		res.put("listStatusFilter_nanos", listStatusFilterStats.getSumNanos());

		res.put("listStatusMulti_count", (long)listStatusMultiStats.getCount());
		res.put("listStatusMulti_nanos", listStatusMultiStats.getSumNanos());

		res.put("listStatusFilterMulti_count", (long)listStatusFilterMultiStats.getCount());
		res.put("listStatusFilterMulti_nanos", listStatusFilterMultiStats.getSumNanos());

		res.put("glob_count", (long)globStats.getCount());
		res.put("glob_nanos", globStats.getSumNanos());

		res.put("globFilter_count", (long)globFilterStats.getCount());
		res.put("globFilter_nanos", globFilterStats.getSumNanos());

		res.put("open_count", (long)openStats.getCount());
		res.put("open_nanos", openStats.getSumNanos());

		res.put("create_count", (long)createStats.getCount());
		res.put("create_nanos", createStats.getSumNanos());

		res.put("createNonRecursive_count", (long)createNonRecursiveStats.getCount());
		res.put("createNonRecursive_nanos", createNonRecursiveStats.getSumNanos());

		res.put("rename_count", (long)renameStats.getCount());
		res.put("rename_nanos", renameStats.getSumNanos());

		res.put("mkdir_count", (long)mkdirStats.getCount());
		res.put("mkdir_nanos", mkdirStats.getSumNanos());

		res.put("delete_count", (long)deleteStats.getCount());
		res.put("delete_nanos", deleteStats.getSumNanos());

		res.put("append_count", (long)appendStats.getCount());
		res.put("append_nanos", appendStats.getSumNanos());

		res.put("concat_count", (long)concatStats.getCount());
		res.put("concat_nanos", concatStats.getSumNanos());

		res.put("truncate_count", (long)truncateStats.getCount());
		res.put("truncate_nanos", truncateStats.getSumNanos());

		res.put("getStatus_count", (long)getStatusStats.getCount());
		res.put("getStatus_nanos", getStatusStats.getSumNanos());

		res.put("listLocatedStatus_count", (long)listLocatedStatusStats.getCount());
		res.put("listLocatedStatus_nanos", listLocatedStatusStats.getSumNanos());

		res.put("listStatusIterator_count", (long)listStatusIteratorStats.getCount());
		res.put("listStatusIterator_nanos", listStatusIteratorStats.getSumNanos());

		res.put("listLocatedFiles_count", (long)listLocatedFilesStats.getCount());
		res.put("listLocatedFiles_nanos", listLocatedFilesStats.getSumNanos());

		res.put("getFileChecksum_count", (long)getFileChecksumStats.getCount());
		res.put("getFileChecksum_nanos", getFileChecksumStats.getSumNanos());

		res.put("getFileChecksumLen_count", (long)getFileChecksumLenStats.getCount());
		res.put("getFileChecksumLen_nanos", getFileChecksumLenStats.getSumNanos());

		res.put("delegationToken_count", (long)delegationTokenStats.getCount());
		res.put("delegationToken_nanos", delegationTokenStats.getSumNanos());
		
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
