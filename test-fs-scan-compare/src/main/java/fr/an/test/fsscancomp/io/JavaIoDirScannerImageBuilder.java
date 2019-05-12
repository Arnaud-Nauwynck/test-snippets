package fr.an.test.fsscancomp.io;

import java.io.File;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;
import fr.an.test.fsscancomp.img.ImageFragmentsWriterUtils;

/**
 * (Slow) Dir scanner using java.io  then file.isFile(),file.lastModified(),file.size()
 *
 */
public class JavaIoDirScannerImageBuilder {

	private ImageEntryHandler fileHandler;
	
	public JavaIoDirScannerImageBuilder(ImageEntryHandler fileHandler) {
		this.fileHandler = fileHandler;
	};

	@Deprecated
	public void recurseScan(File dir) {
		File[] files = dir.listFiles();
		if (files != null && files.length != 0) {
			for(File f : files) {
				boolean isFile = f.isFile();
				long lastModif = f.lastModified();
				long fileLength = (isFile)? f.length() : 0;
				String md5 = null;
				ImageEntry e = new ImageEntry(isFile, f.getPath(), lastModif, fileLength, md5);
				
				fileHandler.handle(e);

				if (f.isDirectory()) {
					recurseScan(f);
				}
			}
		}
	}

	public static void scanDirWriteImage(File dir, 
			File toBaseDir, String destImageName, int hashSize, int maxFragmentSize,
			File debugTextFile) {
		ImageEntryHandler entryHandler = ImageFragmentsWriterUtils.createSplitThenSortBufferedWriter(toBaseDir, destImageName, hashSize,
				maxFragmentSize, debugTextFile);
		
		JavaIoDirScannerImageBuilder scanner = new JavaIoDirScannerImageBuilder(entryHandler);
		
		// *** The Biggy ***
		scanner.recurseScan(dir);
		
		entryHandler.close();
	}

}
