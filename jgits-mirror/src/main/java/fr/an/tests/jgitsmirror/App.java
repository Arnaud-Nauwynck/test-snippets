package fr.an.tests.jgitsmirror;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.an.tests.jgitsmirror.GitDir.GitRemote;

public class App {
	
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private ObjectMapper objectMapper = new ObjectMapper();
	private File gitsMirrorFile;
	private GitDirsMirror gitsMirror;
	
	private int displayProgressIndex = 0;
	private int displayProgressFreq = 100;
	
	public static void main(String[] args) {
		new App().run(args);
	}

	private void run(String[] args) {
		for(int i = 0; i < args.length; i++) {
			String a = args[i];
			switch(a) {
			case "--settings":
				gitsMirrorFile = new File(args[++i]);
				break;
			}
		}
		
		if (gitsMirrorFile != null && gitsMirrorFile.exists()) {
			try {
				gitsMirror = objectMapper.readValue(gitsMirrorFile, GitDirsMirror.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			gitsMirrorFile = new File("/home/arnaud/downloadTools/gitsMirror.json");
			gitsMirror = new GitDirsMirror();
		}
		
//		File scanDir = null; // TODO..
//		if (scanDir != null) {
//			gitsMirror = new GitDirsMirror();
//			if (scanDir.exists() && scanDir.isDirectory()) {
//				recursiveScanDir(gitsMirror, scanDir);
//			}
//			
//			System.out.println("Finished scanning .git dirs: " + gitsMirror.getGitDirs());
//			writeGitDirsMirror(gitsMirror);
//		} else {
//			
//		}
		
//		System.out.println("RemoteInfo all ..");
//		remoteInfoAll();
//		writeGitDirsMirror(gitsMirror);
//
//		System.out.println("Fetching all ..");
//		fetchAll();
//		
		System.out.println("Pulling all ..");
		pullAll();
		
		System.out.println("GC all ..");
		gcAll();
		
		
		// executorService.awaitTermination(1, TimeUnit.MINUTES);
		executorService.shutdownNow();
	}

	private void writeGitDirsMirror(GitDirsMirror gitsMirror) {
		try {
			objectMapper.writeValue(gitsMirrorFile, gitsMirror);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private void recursiveScanDir(GitDirsMirror result, File dir) {
		File gitDir = new File(dir, ".git");
		if (gitDir.exists()) {
			result.getGitDirs().add(new GitDir(dir.getAbsolutePath()));
			System.out.println(gitDir);
			
			if (result.getGitDirs().size() % 50 == 0) {
				System.out.println("... writing " + gitsMirrorFile);
				writeGitDirsMirror(result);
			}
		} else {
			File[] childFiles = dir.listFiles();
			if (childFiles != null && childFiles.length > 0) {
				for(File childFile : childFiles) {
					if (childFile.isFile()) {
						continue;
					}
					String fileName = childFile.getName();
					if (fileName.equals("src") || fileName.equals("target")) {
						continue; // do not scan..
					}
					recursiveScanDir(result, childFile);
				}
			}
		}
	}
	
	public void fetchAll() {
		List<Future<String>> tasks = new ArrayList<>();
		for(GitDir gitDir : gitsMirror.getGitDirs()) {
			tasks.add(executorService.submit(() -> fetch(gitDir)));
		}
		awaitAllTasks(tasks);
	}

	public void pullAll() {
		List<Future<String>> tasks = new ArrayList<>();
		for(GitDir gitDir : gitsMirror.getGitDirs()) {
			tasks.add(executorService.submit(() -> pull(gitDir)));
		}
		awaitAllTasks(tasks);
	}
	
	public void gcAll() {
		List<Future<String>> tasks = new ArrayList<>();
		for(GitDir gitDir : gitsMirror.getGitDirs()) {
			tasks.add(executorService.submit(() -> gc(gitDir)));
		}
		awaitAllTasks(tasks);
	}
	
	public void remoteInfoAll() {
		List<Future<String>> tasks = new ArrayList<>();
		for(GitDir gitDir : gitsMirror.getGitDirs()) {
			tasks.add(executorService.submit(() -> remoteInfo(gitDir)));
		}
		awaitAllTasks(tasks);
	}
	
	private <T> List<T> awaitAllTasks(List<Future<T>> tasks) {
		List<T> res = new ArrayList<>();
		for(Future<T> task : tasks) {
			try {
				res.add(task.get());
			} catch (Exception ex) {
				System.out.println("Failed " + ex.getMessage());
				// throw new RuntimeException("Failed", ex);
			}
		}
		return res;
	}
	
	public String fetch(GitDir gitDir) {
		// System.out.println("fetching git " + gitDir.dir);
		long startTime = System.currentTimeMillis();
		
		Git git;
		try {
			git = Git.init().setDirectory(new File(gitDir.dir)).call();
		} catch (IllegalStateException | GitAPIException ex) {
			throw new RuntimeException("Failed to init git '" + gitDir.dir + "'", ex);
		}
		
		git.fetch();

		long millis = System.currentTimeMillis() - startTime;
		if (millis > 5000) {
			System.out.println(".. done fetching git " + gitDir.dir + ", took " + (millis/1000) + " s");
		}
		incProgress();
		return gitDir.dir + ", took " + millis + " ms";
	}
	
	
	public String pull(GitDir gitDir) {
		// System.out.println("pulling git " + gitDir.dir);
		long startTime = System.currentTimeMillis();
		
		Git git;
		try {
			git = Git.init().setDirectory(new File(gitDir.dir)).call();
		} catch (IllegalStateException | GitAPIException ex) {
			throw new RuntimeException("Failed to init git '" + gitDir.dir + "'", ex);
		}
		
		Status gitStatusRes;
		try {
			gitStatusRes = git.status().call();
		} catch (NoWorkTreeException | GitAPIException ex) {
			throw new RuntimeException("Failed to git status '" + gitDir.dir + "'", ex);
		}
		if (gitStatusRes.getModified().isEmpty()) {
			git.pull();
		} else {
			// modif!
			System.out.println("detected modifs '" + gitDir.dir + "' .. no pull");
		}

		long millis = System.currentTimeMillis() - startTime;
		if (millis > 5000) {
			System.out.println(".. done pulling git " + gitDir.dir + ", took " + (millis/1000) + " s");
		}
		incProgress();
		return gitDir.dir + ", took " + millis + " ms";
	}
	
	public String gc(GitDir gitDir) {
		// System.out.println("pulling git " + gitDir.dir);
		long startTime = System.currentTimeMillis();
		
		Git git;
		try {
			git = Git.init().setDirectory(new File(gitDir.dir)).call();
		} catch (IllegalStateException | GitAPIException ex) {
			throw new RuntimeException("Failed to init git '" + gitDir.dir + "'", ex);
		}
		
		git.gc();
		
		long millis = System.currentTimeMillis() - startTime;
		if (millis > 5000) {
			System.out.println(".. done gc git " + gitDir.dir + ", took " + (millis/1000) + " s");
		}
		incProgress();
		return gitDir.dir + ", took " + millis + " ms";
	}
	
	
	public String remoteInfo(GitDir gitDir) {
		// System.out.println("remoteInfo git " + gitDir.dir);
		long startTime = System.currentTimeMillis();
		
		Git git;
		try {
			git = Git.init().setDirectory(new File(gitDir.dir)).call();
		} catch (IllegalStateException | GitAPIException ex) {
			throw new RuntimeException("Failed to init git '" + gitDir.dir + "'", ex);
		}
		
		try {
			if (gitDir.remotes.isEmpty()) {
				gitDir.remotes.clear();
				List<RemoteConfig> remoteList = git.remoteList().call();
				for(RemoteConfig remoteCfg : remoteList) {
					String remoteName = remoteCfg.getName();
					if (remoteName.equals("origin")) {
						List<URIish> uris = remoteCfg.getURIs();
						if (uris.size() == 1) {
							URIish uri0 = uris.get(0);
							String uriStr = uri0.toString();
							GitRemote gitRemote = new GitRemote(remoteName, uriStr);
							gitDir.remotes.add(gitRemote);
						}
					}
				}
			}
				
//			List<Ref> branches = git.branchList().call();
			
		} catch (GitAPIException e) {
			e.printStackTrace();
		}

		long millis = System.currentTimeMillis() - startTime;
		if (millis > 3000) {
			System.out.println(".. done remoteInfo git " + gitDir.dir + ", took " + (millis/1000) + " s");
		}
		incProgress();
		return gitDir.dir + ", took " + millis + " ms";
	}
	
	private void incProgress() {
		displayProgressIndex++;
		if (displayProgressIndex >= displayProgressFreq) {
			displayProgressIndex = 0;
			System.out.print(".");
		}
	}
	
}
