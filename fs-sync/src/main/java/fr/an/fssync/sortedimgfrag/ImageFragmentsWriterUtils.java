package fr.an.fssync.sortedimgfrag;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.function.Function;

import fr.an.fssync.model.visitor.FsEntryVisitor;
import fr.an.fssync.model.visitor.utils.HashDispatcherFsEntryVisitor;
import fr.an.fssync.model.visitor.utils.MulticastFsEntryVisitor;
import fr.an.fssync.model.visitor.utils.PrettyPrinterFsEntryVisitor;

public class ImageFragmentsWriterUtils {

    public static FsEntryVisitor createSplitThenSortBufferedWriter(File toBaseDir, String destImageName, int hashSize,
	    int maxFragmentSize, File debugTextFile, Function<FsEntryVisitor, FsEntryVisitor> entryEnricher) {
	FsEntryVisitor[] splitHandlers = new FsEntryVisitor[hashSize];
	for (int i = 0; i < hashSize; i++) {
	    String baseFrag = destImageName + "." + i;
	    splitHandlers[i] = new ImageEntrySortedFragmentsBufferedWriter(toBaseDir, baseFrag, maxFragmentSize);
	}
	FsEntryVisitor hashEntryHandler = new HashDispatcherFsEntryVisitor(splitHandlers);

	FsEntryVisitor entryHandler = hashEntryHandler;
	if (debugTextFile != null) {
	    PrintStream debugOut;
	    try {
		debugOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(debugTextFile)));
	    } catch (FileNotFoundException ex) {
		throw new RuntimeException("Failed", ex);
	    }
	    FsEntryVisitor debugHandler = new PrettyPrinterFsEntryVisitor(debugOut);

	    entryHandler = new MulticastFsEntryVisitor(new FsEntryVisitor[] { hashEntryHandler, debugHandler });
	}
	FsEntryVisitor res = (entryEnricher != null) ? entryEnricher.apply(entryHandler) : entryHandler;
	return res;
    }

}
