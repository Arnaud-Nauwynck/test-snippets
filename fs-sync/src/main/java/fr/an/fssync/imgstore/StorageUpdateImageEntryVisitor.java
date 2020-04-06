package fr.an.fssync.imgstore;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.model.visitor.FsEntryVisitor;

public class StorageUpdateImageEntryVisitor extends FsEntryVisitor {

    private FsImageKeyStore dbStorage;

    public StorageUpdateImageEntryVisitor(FsImageKeyStore dbStorage) {
	this.dbStorage = dbStorage;
    }

    @Override
    public void begin() {
    }

    @Override
    public void visit(FsEntry entry) {
	FsPath path = entry.path;
	FsEntryInfo prevEntryInfo = dbStorage.readPathInfo(path);
	if (prevEntryInfo == null || !prevEntryInfo.equals(entry.info)) {
	    dbStorage.writePathInfo(path, entry.info, prevEntryInfo);
	}
    }

    @Override
    public void end() {
	// TODO ...
	// remove entries from dbStorage not seen in visitor
    }

}