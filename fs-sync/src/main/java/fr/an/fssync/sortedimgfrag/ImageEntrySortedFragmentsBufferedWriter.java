package fr.an.fssync.sortedimgfrag;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.TreeSet;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;
import fr.an.fssync.sortedimgfrag.codec.ImageEntryFragmentIncrDataWriter;

public class ImageEntrySortedFragmentsBufferedWriter extends FsEntryVisitor {

    private final File baseDir;
    private final String baseFragmentName;
    private final int maxFragmentSize;

    private int currFragmentIndex;

    private TreeSet<FsEntry> currSortedBuffer;

    public ImageEntrySortedFragmentsBufferedWriter(File baseDir, String baseFragmentName, int maxFragmentSize) {
	this.baseDir = baseDir;
	this.baseFragmentName = baseFragmentName;
	this.maxFragmentSize = maxFragmentSize;
	this.currFragmentIndex = 0;
	this.currSortedBuffer = new TreeSet<>(FsEntry.PATH_COMPARATOR);
    }

    @Override
    public void visit(FsEntry e) {
	currSortedBuffer.add(e);
	if (currSortedBuffer.size() > maxFragmentSize) {
	    flush();
	}
    }


    @Override
    public void begin() {
    }

    @Override
    public void end() {
	flush();
    }

    private void flush() {
	File currFile = new File(baseDir, baseFragmentName + "." + currFragmentIndex);
	try (OutputStream out = new BufferedOutputStream(new FileOutputStream(currFile))) {
	    ImageEntryFragmentIncrDataWriter writer = new ImageEntryFragmentIncrDataWriter(out);
	    writer.begin();
	    for (FsEntry e : currSortedBuffer) {
		writer.visit(e);
	    }
	    writer.end();
	    writer.close();
	    // System.out.println("write sorted fragment " + currFile);
	} catch (Exception ex) {
	    throw new RuntimeException("", ex);
	}
	this.currFragmentIndex++;
	this.currSortedBuffer.clear();
    }

}
