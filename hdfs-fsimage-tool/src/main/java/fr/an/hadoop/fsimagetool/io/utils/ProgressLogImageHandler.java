package fr.an.hadoop.fsimagetool.io.utils;

import fr.an.hadoop.fsimagetool.io.ImageEntry;
import fr.an.hadoop.fsimagetool.io.ImageEntryHandler;

public class ProgressLogImageHandler extends ImageEntryHandler {

	private int logFreq = 20000;
	private int logIndex = 0;
	
	@Override
	public void handle(ImageEntry e) {
		if (--logIndex <= 0) {
			logIndex = logFreq;
			System.out.println(e.path);
		}
	}

	@Override
	public void close() {
	}
	
}
