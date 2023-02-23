package fr.an.tests.resumabledirscan;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResumableDirScanMain {

	File scanDir = new File("c:/");
	File scanLogFile = new File("scan-c.txt");

	final Object lock = new Object();
	
	List<String> remainSubPaths = new ArrayList<>(10000);

	Set<String> pendingSubPaths = new HashSet<>();

	// ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(6);

	long maxPartialMillis = 120_000;
	boolean slownDown = false;
	
	public static void main(String[] args) {
		try {
			val app = new ResumableDirScanMain();
			app.run();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void run() {
		ObjectMapper om = new ObjectMapper();
		
		
		// read already scanned (nd-json files)
		if (scanLogFile.exists()) {
			try(BufferedReader lineReader = new BufferedReader(new InputStreamReader(new FileInputStream(scanLogFile)))) {
				String line;
				while(null != (line = lineReader.readLine())) {
					val partialScanDirResult = om.readValue(line, PartialScanDirResultDTO.class);
					// TODO
				}
			} catch(Exception ex) {
				log.error("Failed to read file.. ignore", ex);
			}
		} else {
			log.info("no scan log file .. init");
			remainSubPaths.add(".");
		}
		
		
		// resume scan some remaining, append partial results to file..
		try(val out = new BufferedOutputStream(new FileOutputStream(scanLogFile, true))) {
			
			for(;;) {
				int remainCount = remainSubPaths.size();
				if (remainCount == 0 && pendingSubPaths.size()==0) {
					log.info("Finished!");
					break;
				}
				val subPath = remainSubPaths.remove(remainCount-1);
				pendingSubPaths.add(subPath);
				
				// submit scanning currScanDir to thread pool
				// TODO
				val partialRes = partialScanDir(subPath);
			
				// om.writeValue(out, partialRes);
				String writeLine = om.writeValueAsString(partialRes);
				System.out.println(writeLine);

				out.write(writeLine.getBytes());
				out.write('\n');
				out.flush(); // ??
				
				if (partialRes.remainSubPaths != null && ! partialRes.remainSubPaths.isEmpty()) {
					remainSubPaths.addAll(partialRes.remainSubPaths);
				}
				pendingSubPaths.remove(subPath);
			}
			
			// TODO
		} catch(Exception ex) {
			log.error("Failed to write file.. ignore", ex);
		}
		
	}
	
	PartialScanDirResultDTO partialScanDir(String subPath) {
		val startTime = System.currentTimeMillis();
		val res = new PartialScanDirResultDTO();
		res.scannedTime = startTime;
		res.stat = new DirStatDTO();
		res.dir = subPath;
		// val maxTime = startTime + maxPartialMillis;
		File subDir = (!subPath.equals("."))? new File(scanDir, subPath) : scanDir;
		
		// recurse
		recursePartialScanDir(res, subPath, subDir);
		
		res.scannedMillis = (int)(System.currentTimeMillis() - startTime);
		return res;
	}

	private void recursePartialScanDir(PartialScanDirResultDTO res, String subPath, File subDir) {
		val maxTime = res.scannedTime + maxPartialMillis;
		val childLs = subDir.listFiles();
		if (childLs == null) {
			return;
		}
		for(val child: childLs) {
			val childName = child.getName();
			val childPath = subPath + "/" + childName;
			if (child.isFile()) {
				val fileLen = child.length();
				res.stat.fileCount++;
				res.stat.fileTotalSize += fileLen;
				res.stat.fileMaxSize = Math.max(res.stat.fileMaxSize, fileLen);
				
				debugSlowdown();
			} else {
				// recurse..
				val now = System.currentTimeMillis();
				if (now < maxTime) {
					val childDir = new File(subDir, childName);
					recursePartialScanDir(res, childPath, childDir);
				} else {
					// stop loop and recurse.. append to remaining
					res.remainSubPaths.add(childPath);
				}
			}
		}
	}

	private void debugSlowdown() {
		if (slownDown) {
			// slow down for debug
			try {
				Thread.sleep(10); 
			} catch(Exception ex) {
			}
		}
	}
}
