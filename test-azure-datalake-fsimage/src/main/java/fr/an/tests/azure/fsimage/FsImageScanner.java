package fr.an.tests.azure.fsimage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.hadoop.hdfs.inotify.Event.CreateEvent.INodeType;

import com.azure.core.http.rest.PagedFlux;
import com.azure.storage.file.datalake.DataLakeDirectoryAsyncClient;
import com.azure.storage.file.datalake.models.PathItem;

import fr.an.fssync.model.FsEntry;
import fr.an.fssync.model.FsEntryInfo;
import fr.an.fssync.model.FsPath;
import fr.an.fssync.model.visitor.FsEntryVisitor;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * 
 */
public class FsImageScanner {

	protected DataLakeDirectoryAsyncClient rootDirClient;

	protected ExecutorService executor;
	
	protected FsEntryVisitor output;
	
	// ------------------------------------------------------------------------
	
	public FsImageScanner(
			DataLakeDirectoryAsyncClient rootDirClient,
			ExecutorService executor) {
		this.rootDirClient = rootDirClient;
		this.executor = executor;
	}

	// ------------------------------------------------------------------------

	public void scan() {
		FsPath rootPath = FsPath.ROOT; 
		
		// PathProperties rootProps = rootDirClient.getProperties().block(); 
		FsEntryInfo rootEntryInfo = FsEntryInfo.builder()
				.iNodeType(INodeType.DIRECTORY)
				.build();
		FsEntry rootEntry = new FsEntry(rootPath, rootEntryInfo); 

		Future<ScannedEntry> rootScannedFuture = submitRecursiveScanDir(rootDirClient, rootEntry);
		
		output.begin();
		recursiveBuildRes(rootScannedFuture);
		output.end();
	}


	@AllArgsConstructor
	private static class ScannedEntry {
		FsEntry entry;
		Throwable scanChildEx;
		List<Future<ScannedEntry>> childFutures;
	}
	
	private void recursiveBuildRes(Future<ScannedEntry> entryFuture) {
		ScannedEntry scannedEntry;
		try {
			scannedEntry = entryFuture.get();
		} catch (Exception ex) {
			throw new RuntimeException("Failed", ex);
		}
		FsEntry fsEntry = scannedEntry.entry;
		output.visit(fsEntry);
		if (! fsEntry.isFile()) {
			if (scannedEntry.scanChildEx != null) {
				// log.warn("Failed to scan child..");
			}
			if (scannedEntry.childFutures != null) { // may be partial if error occured
				for(Future<ScannedEntry> childFuture: scannedEntry.childFutures) {
					recursiveBuildRes(childFuture);
				}
			}
		}
	}
	
	private Future<ScannedEntry> submitRecursiveScanDir(
			DataLakeDirectoryAsyncClient currDirClient,
			FsEntry currEntry
			) {
		CompletableFuture<ScannedEntry> resFuture = new CompletableFuture<>(); // completed in async callback below
		
		// use ThreadPool...
		executor.submit(() -> {
			Map<String,Future<ScannedEntry>> childScanFutures = new TreeMap<>();
			val currPath = currEntry.path;
			
			// call async listPaths() with PagedFlux callbacks: (onItem, onError, onComplete)
			PagedFlux<PathItem> childLsFlux = currDirClient.listPaths();
			childLsFlux.subscribe(childItem -> {
				// callback scanned 1 child
				String childName = childNameOf(childItem);
				FsPath childPath = currPath.child(childName);
				
				FsEntry childEntry = simplePathItemToFsEntry(childItem, childPath);
				Future<ScannedEntry> childScanFuture;
				if (childItem.isDirectory()) {
					DataLakeDirectoryAsyncClient childDirClient = currDirClient.getSubdirectoryAsyncClient(childName);
					// *** THE BIGGY : recurse ***
					childScanFuture = submitRecursiveScanDir(childDirClient, childEntry);
				} else {
					// no need to enhance file info (yet)
					// TOADD: childScanFuture = executor.submit(() -> scanFile(..));
					val childScannedEntry = new ScannedEntry(childEntry, null, null);
					childScanFuture = CompletableFuture.completedFuture(childScannedEntry);
				}
				childScanFutures.put(childName, childScanFuture);
			}, error -> {
				// callback: error scanning child list!!
				val child = new ArrayList<Future<ScannedEntry>>(childScanFutures.values());
				ScannedEntry resEntry = new ScannedEntry(currEntry, error, child);
				resFuture.complete(resEntry);
			}, () -> {
				// callback: complete child list
				val child = new ArrayList<Future<ScannedEntry>>(childScanFutures.values());
				ScannedEntry resEntry = new ScannedEntry(currEntry, null, child);
				resFuture.complete(resEntry);
			});
		});

		return resFuture;
	}
	
	
	protected static String childNameOf(PathItem item) {
		String pathName = item.getName();
		int lastSepIndex = pathName.lastIndexOf('/');
		return pathName.substring(lastSepIndex + 1);
	}
	
	
	protected FsEntry simplePathItemToFsEntry(PathItem item, FsPath path) {
		FsEntryInfo pathInfo = FsEntryInfo.builder()
			    .iNodeType(item.isDirectory()? INodeType.DIRECTORY : INodeType.FILE)
//			    .ctime(item.)
			    .mtime(item.getLastModified().toEpochSecond())
//			    
//			    .ownerName()
//			    .groupName()
//			    .perms()
//			    .aclEntries()
//			    
			    .fileSize(item.getContentLength())
//			    
//			    .md5()
//			    .extraDataMarker()

				.build();
		return new FsEntry(path, pathInfo);
	}

}
