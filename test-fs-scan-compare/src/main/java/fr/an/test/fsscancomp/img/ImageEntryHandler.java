package fr.an.test.fsscancomp.img;

import java.io.Closeable;

public abstract class ImageEntryHandler implements Closeable {

	public abstract void handle(ImageEntry entry);

	@Override
	public abstract void close();
	
}
