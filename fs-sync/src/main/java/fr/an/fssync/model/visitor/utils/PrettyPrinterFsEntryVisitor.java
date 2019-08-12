package fr.an.fssync.model.visitor.utils;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;
import lombok.val;

public class PrettyPrinterFsEntryVisitor extends FsEntryVisitor {

    private PrintStream out;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss");

    public PrettyPrinterFsEntryVisitor(PrintStream out) {
	this.out = out;
    }

    @Override
    public void begin() {
    }

    @Override
    public void end() {
    }

    @Override
    public void visit(FsEntry e) {
	val info = e.info;
	boolean isFile = info.isFile();
	out.print("'" + e.path + "' " + (isFile ? "f" : "d") //
		+ " " + df.format(new Date(info.mtime))
		+ (isFile ? " " + info.fileSize : "") //
		+ "\n");
    }

}
