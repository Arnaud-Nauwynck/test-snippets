package fr.an.fssync.model.visitor.utils;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;

public class ProgressLogFsEntryVisitor extends FsEntryVisitor {

    private int logFreq = 20000;
    private int logIndex = 0;

    @Override
    public void visit(FsEntry e) {
	if (--logIndex <= 0) {
	    logIndex = logFreq;
	    System.out.println(e.path);
	}
    }

    @Override
    public void begin() {
    }

    @Override
    public void end() {
    }

}
