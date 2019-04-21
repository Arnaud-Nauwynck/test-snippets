package fr.an.hadoop.fsimagetool.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import fr.an.hadoop.fsimagetool.io.utils.MulticastImageHandler;
import fr.an.hadoop.fsimagetool.io.utils.PrettyPrinterImageHandler;
import fr.an.hadoop.fsimagetool.io.utils.ProgressLogImageHandler;

public class ImageFragmentsReaderTest {

	@Test
	public void testReadImage() {
		File imageBaseDir = new File("out2");
		String baseImageName = "img";

		File resFile = new File(imageBaseDir, "img-result-sorted-all.txt");
		PrintStream resOut;
		try {
			resOut = new PrintStream(
					new BufferedOutputStream(new FileOutputStream(resFile)));
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Failed", ex);
		}
		ImageEntryHandler resHandler = new PrettyPrinterImageHandler(resOut);
		
		ImageEntryHandler imageHandler = new MulticastImageHandler(new ImageEntryHandler[] {
				new ProgressLogImageHandler(),
				resHandler
		});

		
		ImageFragmentsReader.readImage(imageBaseDir, baseImageName, 
				imageHandler);
	}

}
