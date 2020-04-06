package fr.an.fssync.model.visitor;

import fr.an.fssync.model.FsEntry;

public abstract class FsEntryVisitor {

    public abstract void begin();
    
    public abstract void visit(FsEntry entry);

    public abstract void end();
	
}
