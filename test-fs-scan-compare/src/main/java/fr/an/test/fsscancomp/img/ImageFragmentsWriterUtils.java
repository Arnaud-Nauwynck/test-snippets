package fr.an.test.fsscancomp.img;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.function.Function;

import fr.an.test.fsscancomp.utils.HashDispatcherImageHandler;
import fr.an.test.fsscancomp.utils.ImageEntrySortedFragmentsBufferedWriter;
import fr.an.test.fsscancomp.utils.MulticastImageHandler;
import fr.an.test.fsscancomp.utils.PrettyPrinterImageHandler;

public class ImageFragmentsWriterUtils {

	public static ImageEntryHandler createSplitThenSortBufferedWriter(
			File toBaseDir, String destImageName,
			int hashSize, int maxFragmentSize, 
			File debugTextFile,
			Function<ImageEntryHandler,ImageEntryHandler> entryEnricher) {
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
		ImageEntryHandler res = (entryEnricher != null)? entryEnricher.apply(entryHandler) : entryHandler;
		return res;
	}
	
}
