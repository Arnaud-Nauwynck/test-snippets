package fr.an.fssync.img;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import fr.an.fssync.model.visitor.FsEntryVisitor;
import fr.an.fssync.model.visitor.utils.MulticastFsEntryVisitor;
import fr.an.fssync.model.visitor.utils.PrettyPrinterFsEntryVisitor;
import fr.an.fssync.model.visitor.utils.ProgressLogFsEntryVisitor;
import fr.an.fssync.sortedimgfrag.ImageFragmentsReader;

public class ImageFragmentsReaderTest {

    @Test
    public void testReadImage() {
	File imageBaseDir = new File("out2");
	String baseImageName = "img";

	File resFile = new File(imageBaseDir, "img-result-sorted-all.txt");
	PrintStream resOut;
	try {
	    resOut = new PrintStream(new BufferedOutputStream(new FileOutputStream(resFile)));
	} catch (FileNotFoundException ex) {
	    throw new RuntimeException("Failed", ex);
	}
	FsEntryVisitor resHandler = new PrettyPrinterFsEntryVisitor(resOut);

	FsEntryVisitor imageHandler = new MulticastFsEntryVisitor(
		new FsEntryVisitor[] { new ProgressLogFsEntryVisitor(), resHandler });

	ImageFragmentsReader.readImage(imageBaseDir, baseImageName, imageHandler);
    }

}
