package fr.an.fssync.model.visitor.utils;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.visitor.FsEntryVisitor;

public class HashDispatcherFsEntryVisitor extends FsEntryVisitor {

    private final FsEntryVisitor[] split;

    public HashDispatcherFsEntryVisitor(FsEntryVisitor[] split) {
	this.split = split;
    }

    @Override
    public void visit(FsEntry e) {
	int hash = Math.abs(e.path.hashCode()) % split.length;
	split[hash].visit(e);
    }

    @Override
    public void begin() {
	for (FsEntryVisitor h : split) {
	    h.begin();
	}
    }

    @Override
    public void end() {
	for (FsEntryVisitor h : split) {
	    h.end();
	}
    }

}
