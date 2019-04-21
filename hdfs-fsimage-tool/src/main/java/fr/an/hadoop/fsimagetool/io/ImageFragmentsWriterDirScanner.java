package fr.an.hadoop.fsimagetool.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import fr.an.hadoop.fsimagetool.io.utils.HashDispatcherImageHandler;
import fr.an.hadoop.fsimagetool.io.utils.ImageEntrySortedFragmentsBufferedWriter;
import fr.an.hadoop.fsimagetool.io.utils.MulticastImageHandler;
import fr.an.hadoop.fsimagetool.io.utils.PrettyPrinterImageHandler;

public class ImageFragmentsWriterDirScanner {

	public static void scanDirWriteImage(File dir, 
			File toBaseDir, String destImageName, int hashSize, int maxFragmentSize,
			File debugTextFile) {
		ImageEntryHandler[] splitHandlers = new ImageEntryHandler[hashSize];
		for(int i = 0; i < hashSize; i++) {
			String baseFrag = destImageName + "." + i;
			splitHandlers[i] = new ImageEntrySortedFragmentsBufferedWriter(toBaseDir, baseFrag, maxFragmentSize);
		}
		ImageEntryHandler hashEntryHandler = new HashDispatcherImageHandler(splitHandlers);
		
		ImageEntryHandler entryHandler = hashEntryHandler;
		if (debugTextFile != null) {
			PrintStream debugOut;
			try {
				debugOut = new PrintStream(
						new BufferedOutputStream(new FileOutputStream(debugTextFile)));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException("Failed", ex);
			}
			ImageEntryHandler debugHandler = new PrettyPrinterImageHandler(debugOut);
			
			entryHandler = new MulticastImageHandler(new ImageEntryHandler[] {
					hashEntryHandler,
					debugHandler
			});
		}
		
		DirScannerImageBuilder scanner = new DirScannerImageBuilder(entryHandler);
		
		// *** The Biggy ***
		scanner.recurseScan(dir);
		
		entryHandler.close();
	}
	
}
