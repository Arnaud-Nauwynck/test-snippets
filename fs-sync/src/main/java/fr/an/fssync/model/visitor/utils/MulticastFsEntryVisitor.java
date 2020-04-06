package fr.an.fssync.model.visitor.utils;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;

public class MulticastFsEntryVisitor extends FsEntryVisitor {

    private FsEntryVisitor[] listeners;

    public MulticastFsEntryVisitor(FsEntryVisitor[] listeners) {
	this.listeners = listeners;
    }

    @Override
    public void begin() {
	for (FsEntryVisitor listener : listeners) {
	    listener.begin();
	}
    }

    @Override
    public void end() {
	for (FsEntryVisitor listener : listeners) {
	    listener.end();
	}
    }

    @Override
    public void visit(FsEntry e) {
	for (FsEntryVisitor listener : listeners) {
	    listener.visit(e);
	}
    }


}
