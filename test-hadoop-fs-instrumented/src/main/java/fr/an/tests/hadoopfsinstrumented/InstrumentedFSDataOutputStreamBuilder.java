package fr.an.tests.hadoopfsinstrumented;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FSDataOutputStreamBuilder;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystem.Statistics;

import fr.an.tests.hadoopfsinstrumented.stats.InstrumentedFSOutputStreamStats;

import org.apache.hadoop.fs.Path;

public class InstrumentedFSDataOutputStreamBuilder
	extends FSDataOutputStreamBuilder<InstrumentedFSDataOutputStream,InstrumentedFSDataOutputStreamBuilder> {

	protected final InstrumentedHadoopFileSystem fileSystem; // redundant with private super.fileSystem
	
	protected final Path path; // redundant with private super.path
	
	protected final FSDataOutputStreamBuilder<?,?> delegate;
	
	protected final InstrumentedFSOutputStreamStats outputEntryStats;

	// ------------------------------------------------------------------------
	
	protected InstrumentedFSDataOutputStreamBuilder(
			@Nonnull InstrumentedHadoopFileSystem fileSystem, @Nonnull Path path,
			FSDataOutputStreamBuilder<?,?> delegate,
			InstrumentedFSOutputStreamStats outputEntryStats
			) {
		super(fileSystem, path);
		this.fileSystem = fileSystem;
		this.path = path;
		this.delegate = delegate;
		this.outputEntryStats = outputEntryStats;
	}

	// ------------------------------------------------------------------------

	@Override
	public InstrumentedFSDataOutputStreamBuilder getThisBuilder() {
		return this;
	}

	@Override
	public InstrumentedFSDataOutputStream build() throws IllegalArgumentException, IOException {
		FSDataOutputStream delegateOutputStream = delegate.build();
		@SuppressWarnings("deprecation")
		Statistics delegateStatistics = FileSystem.getStatistics(fileSystem.getScheme(), fileSystem.getClass());
		// StorageStatistics storageStatistics = FileSystem.getGlobalStorageStatistics().get(delegateFs.getScheme());
		return new InstrumentedFSDataOutputStream(delegateOutputStream, delegateStatistics, path, outputEntryStats);
	}

}
