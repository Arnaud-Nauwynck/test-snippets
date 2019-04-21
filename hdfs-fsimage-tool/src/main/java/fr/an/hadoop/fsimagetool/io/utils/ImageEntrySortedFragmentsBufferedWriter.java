package fr.an.hadoop.fsimagetool.io.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.TreeSet;

import fr.an.hadoop.fsimagetool.io.ImageEntry;
import fr.an.hadoop.fsimagetool.io.ImageEntryHandler;
import fr.an.hadoop.fsimagetool.io.codec.ImageEntryFragmentIncrDataWriter;

public class ImageEntrySortedFragmentsBufferedWriter extends ImageEntryHandler {

	private final File baseDir;
	private final String baseFragmentName;
	private final int maxFragmentSize;

	private int currFragmentIndex;

	private TreeSet<ImageEntry> currSortedBuffer;
	
	
	public ImageEntrySortedFragmentsBufferedWriter(File baseDir, 
			String baseFragmentName,
			int maxFragmentSize) {
		this.baseDir = baseDir;
		this.baseFragmentName = baseFragmentName;
		this.maxFragmentSize = maxFragmentSize;
		this.currFragmentIndex = 0;
		this.currSortedBuffer = new TreeSet<>(ImageEntry.PATH_COMPARATOR);
	}


	@Override
	public void handle(ImageEntry e) {
		currSortedBuffer.add(e);
		if (currSortedBuffer.size() > maxFragmentSize) {
			flush();
		}
	}

	@Override
	public void close() {
		flush();
	}

	private void flush() {
		File currFile = new File(baseDir, baseFragmentName + "." + currFragmentIndex);
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(currFile))) {
			ImageEntryFragmentIncrDataWriter writer = new ImageEntryFragmentIncrDataWriter(out);
			for(ImageEntry e : currSortedBuffer) {
				writer.handle(e);
			}
			writer.close();
			// System.out.println("write sorted fragment " + currFile);
		} catch(Exception ex) {
			throw new RuntimeException("", ex);
		}
		this.currFragmentIndex++;
		this.currSortedBuffer.clear();
	}

}
