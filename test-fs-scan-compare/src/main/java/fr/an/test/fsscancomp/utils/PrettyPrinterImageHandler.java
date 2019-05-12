package fr.an.test.fsscancomp.utils;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.an.test.fsscancomp.img.ImageEntry;
import fr.an.test.fsscancomp.img.ImageEntryHandler;

public class PrettyPrinterImageHandler extends ImageEntryHandler {

	private PrintStream out;
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");
	
	public PrettyPrinterImageHandler(PrintStream out) {
		this.out = out;
	}

	@Override
	public void handle(ImageEntry e) {
		boolean isFile = e.isFile;
		out.print("'" + e.path + "' "
				+ (isFile? "f" : "d") + " "
				+ df.format(new Date(e.lastModified))
				+ (isFile? " " + e.length: "")
				+ "\n");
	}

	@Override
	public void close() {
	}
	
}
