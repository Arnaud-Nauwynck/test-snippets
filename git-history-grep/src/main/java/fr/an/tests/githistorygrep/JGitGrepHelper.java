package fr.an.tests.githistorygrep;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * 
 * cf example: https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/api/
 *
 */
public class JGitGrepHelper {

	private Git git;
	
	public JGitGrepHelper(File gitDir) {
		try {
			InitCommand gitInit = Git.init().setDirectory(gitDir);
			if (! new File(gitDir, ".git").exists()) {
				gitInit.setBare(true);
			}
			git = gitInit.call();
		} catch (IllegalStateException | GitAPIException ex) {
			throw new RuntimeException("Failed to init git '" + gitDir + "'", ex);
		}
	}
	
	
	public List<String> grepInDiff(Date fromDate, Date toDate, Pattern filePathPattern, Pattern textPattern) {
		List<String> res = new ArrayList<>();
		try {
			Repository repository = git.getRepository();
			Iterable<RevCommit> revCommits = git.log().all().call();
			for(RevCommit revCommit : revCommits) {
				RevTree tree = revCommit.getTree();

				int parentCount = revCommit.getParentCount();
				if (parentCount != 1) {
					// merge ... not grep..
					// RevCommit[] parents = revCommit.getParents();
					continue;
				}
				
				RevCommit parentCommit = revCommit.getParent(0);
				
		        List<DiffEntry> diffs = git.diff()
		                .setOldTree(prepareTreeParser(repository, parentCommit))
		                .setNewTree(prepareTreeParser(repository, revCommit))
		                .call();
		        for(DiffEntry diff : diffs) {
		        	String oldPath = diff.getOldPath();
		        	String newPath = diff.getNewPath();
		        	if (!( filePathPattern.matcher(oldPath).matches() || filePathPattern.matcher(newPath).matches())) {
		        		continue;
		        	}
		        	AbbreviatedObjectId oldId = diff.getOldId();
		        	AbbreviatedObjectId newId = diff.getNewId();
		        	
		        	if (oldId.name().startsWith("00000")) {
		        		continue; // first commit?
		        	}
		        	BufferedReader oldLineReader = openReaderForAbbreviatedObjectId(repository, oldId);
		        	List<String> oldLines = grepLines(oldLineReader, textPattern);

		        	BufferedReader newLineReader = openReaderForAbbreviatedObjectId(repository, newId);
		        	List<String> newLines = grepLines(newLineReader, textPattern);
		        	if (oldLines.equals(newLines)) {
		        		continue;
		        	}
		        	
		        	if (!oldLines.isEmpty() || !newLines.isEmpty()) {
		        		res.add("commit:" + revCommit.abbreviate(8).name() 
		        				+ " " + revCommit.getFullMessage()
		        				+ " file: '" + newPath + "' " + ((!oldPath.equals(newPath))? "oldFile:" + oldPath : "") + "\n"
		        				+ "  newLines:\n" + String.join("\n", newLines) + "\n"
		        				+ "  oldLines:\n" + String.join("\n", oldLines) + "\n"
		        				);
		        	}
		        }
		        
		        
			}
			
		} catch(Exception ex) {
			throw new RuntimeException("Failed", ex);
		}
		return res;
	}
	

    private static AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }
    
	
	public List<String> grep(Date fromDate, Date toDate, Pattern filePathPattern, Pattern textPattern) {
		List<String> res = new ArrayList<>();
		try {
			Repository repository = git.getRepository();
			Iterable<RevCommit> revCommits = git.log().all().call();
			for(RevCommit revCommit : revCommits) {
				RevTree tree = revCommit.getTree();
				
				TreeWalk treeWalk = new TreeWalk(repository);
		        treeWalk.addTree(tree);
		        treeWalk.setRecursive(true);
		        //?? treeWalk.setFilter(PathFilter.create(""));
		        while (treeWalk.next()) {
		        	String filePath = treeWalk.getPathString();
		        	if (! filePathPattern.matcher(filePath).matches()) {
		        		continue;
		        	}
		        	
		        	ObjectId objectId = treeWalk.getObjectId(0);
		        	BufferedReader lineReader = openReaderForObjectId(repository, objectId);
		        	for(String line : grepLines(lineReader, textPattern)) {
	        			res.add("commit:" + revCommit.abbreviate(8).name() 
	        				+ " " + revCommit.getFullMessage()
	        				// + revCommit
	        				+ " file: '" + filePath + "' " + line);
		        	}
		        	
		        }
		        treeWalk.close();
			}
			
		} catch(Exception ex) {
			throw new RuntimeException("Failed", ex);
		}
		return res;
	}
	
	public static BufferedReader openReaderForObjectId(Repository repository, ObjectId objectId) throws IOException {
    	ObjectLoader loader = repository.open(objectId);
    	byte[] blobBytes = loader.getBytes();
    	return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(blobBytes)));
	}

	public static BufferedReader openReaderForAbbreviatedObjectId(Repository repository, AbbreviatedObjectId abbreviatedObjectId) throws IOException {
		ObjectReader objectReader = repository.newObjectReader();
		Collection<ObjectId> objectIds = objectReader.resolve(abbreviatedObjectId);
		ObjectId objectId = null;
		if (!objectIds.isEmpty()) {
			objectId = objectIds.iterator().next(); // heuristic!
		} else {
			return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(new byte[0]))); // should not occurs: not found?!
		}
		ObjectLoader loader = repository.open(objectId);
    	byte[] blobBytes = loader.getBytes();
    	return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(blobBytes)));
	}

	public static List<String> grepLines(BufferedReader inputReader, Pattern textPattern) throws IOException {
		List<String> res = new ArrayList<>();
		String line = null;
    	while((line = inputReader.readLine()) != null) {
    		if (textPattern.matcher(line).matches()) {
    			res.add(line);
    		}
    	}
		return res;
	}
	
}
