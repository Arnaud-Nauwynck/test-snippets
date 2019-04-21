package fr.an.hadoop.fsimagetool.io;

import java.io.File;

public class DirScannerImageBuilder {

	private ImageEntryHandler fileHandler;
	private int printFreq = 10_000;
	private int printIndex = 0;
	
	public DirScannerImageBuilder(ImageEntryHandler fileHandler) {
		this.fileHandler = fileHandler;
	};

	public void recurseScan(File dir) {
		File[] files = dir.listFiles();
		if (files != null && files.length != 0) {
			for(File f : files) {
				if (--printIndex <= 0) {
					this.printIndex = printFreq;
					System.out.println(f.getPath());
				}
				boolean isFile = f.isFile();
				long lastModif = f.lastModified();
				long fileLength = (isFile)? f.length() : 0;
				ImageEntry e = new ImageEntry(isFile, f.getPath(), lastModif, fileLength);
				
				fileHandler.handle(e);

				if (f.isDirectory()) {
					recurseScan(f);
				}
			}
		}
	}

}
