package fr.an.hadoop.fsimagetool.io;

public abstract class ImageEntryHandler {

	public abstract void handle(ImageEntry entry);

	public abstract void close();
	
}
