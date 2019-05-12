package fr.an.test.fsscancomp.utils;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;

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
