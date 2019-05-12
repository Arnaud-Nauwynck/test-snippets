package fr.an.test.fsscancomp.utils;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;

public class HashDispatcherImageHandler extends ImageEntryHandler {

	private final ImageEntryHandler[] split;
	
	public HashDispatcherImageHandler(ImageEntryHandler[] split) {
		this.split = split;
	}

	@Override
	public void handle(ImageEntry e) {
		int hash = Math.abs(e.path.hashCode()) % split.length;
		split[hash].handle(e);
	}

	@Override
	public void close() {
		for(ImageEntryHandler h : split) {
			h.close();
		}
	}

}
