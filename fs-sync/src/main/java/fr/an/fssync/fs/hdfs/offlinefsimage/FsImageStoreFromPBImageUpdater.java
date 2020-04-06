package fr.an.fssync.fs.hdfs.offlinefsimage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.an.fssync.fs.hdfs.offlinefsimage.FsImageStoreFromPBImageEntryCallback.FsPathSeen;
import fr.an.fssync.imgstore.FsImageKeyStore;
import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;

/**
 * Helper class to update <code>imageStore</code> from FsImage files
 *
 */
public class FsImageStoreFromPBImageUpdater {

    private FsImageKeyStore imageStore;
    
    public FsImageStoreFromPBImageUpdater(FsImageKeyStore imageStore) {
	this.imageStore = imageStore;
    }

    public void updateImageStore(File fsImage) {
	// in-memory (not efficient..) memento of seen path in fsimage
	Set<FsPath> pathInFsImage = new HashSet<>();
	FsPathSeen pathSeen = new FsPathSeen() {
	    @Override
	    public void put(FsPath path) {
		pathInFsImage.add(path);
	    }
	};
	
	// pass 1/3: iterate on fsimage: handle file,dir,symlink to update in store
	FsImageStoreFromPBImageEntryCallback updateCallback = 
		new FsImageStoreFromPBImageEntryCallback(imageStore, pathSeen);
	PBImageReaderUtils.readFsImage(fsImage, updateCallback);

	// pass 2/3: iterate on imageStore, check path to delete (not seen in fsimage)
	// (TOCHECK can not remove while iterating??)
	List<FsPath> pathesToRemove = new ArrayList<>();

	Iterator<FsEntry> storeIterator = imageStore.entryIterator();
	while(storeIterator.hasNext()) {
	    FsEntry pathWithInfo = storeIterator.next();
	    if (!pathInFsImage.contains(pathWithInfo.path)) {
		pathesToRemove.add(pathWithInfo.path);
	    }
	}
	    
	// pass 3/3: remove pathes to delete
	for(FsPath path : pathesToRemove) {
	    // re-read (not io efficient..)
	    FsEntryInfo prevEntryInfo = imageStore.readPathInfo(path);

	    imageStore.deletePathInfo(path, prevEntryInfo);
	}
	
    }
}
