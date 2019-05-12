package fr.an.test.fsscancomp.utils;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;

public class MulticastImageHandler extends ImageEntryHandler {

	private ImageEntryHandler[] listeners;
	
	public MulticastImageHandler(ImageEntryHandler[] listeners) {
		this.listeners = listeners;
	}

	@Override
	public void handle(ImageEntry e) {
		for(ImageEntryHandler listener : listeners) {
			listener.handle(e);
		}
	}

	@Override
	public void close() {
		for(ImageEntryHandler listener : listeners) {
			listener.close();
		}
	}
	
}
