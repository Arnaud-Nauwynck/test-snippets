package fr.an.hadoop.fsimagetool.io.utils;

import fr.an.hadoop.fsimagetool.io.ImageEntry;
import fr.an.hadoop.fsimagetool.io.ImageEntryHandler;

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
