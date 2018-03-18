package fr.an.tests.githistorygrep;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * sample usage:
 * <PRE>
 * export PRJ_DIR=..
 * git filter-branch --tree-filter 'java -cp ${PRJ_DIR}/target/classes fr.an.tests.githistorygrep.JGitRewriteFilterApp ' HEAD
 * git update-ref -d refs/original/refs/heads/master
 * </PRE>
 * 
 */
public class JGitRewriteFilterApp {

	public static void main(String[] args) {
		File currDir = new File(".").getAbsoluteFile();
		System.out.println("rewrite.. currDir:" + currDir);
		Pattern pattern = Pattern.compile("a=.*");
		
		encodeRewrite(Paths.get("file1.txt"), pattern, "suffix1");
		encodeRewrite(Paths.get("file2.txt"), pattern, "suffix2");
	}

	private static void encodeRewrite(Path file, Pattern pattern, String suffix) {
		if (Files.exists(file)) {
			try {
				List<String> lines = Files.readAllLines(file);
				boolean modified = false;
				List<String> modifiedLines = new ArrayList<>();
				for(String line : lines) {
					if (pattern.matcher(line).matches()) {
						line = line + suffix;
						modified = true;
					}
					modifiedLines.add(line);
				}
				if (modified) {
					System.out.println(".. modified " + file);
					Files.write(file, modifiedLines);
				}
			} catch(Exception ex) {
				throw new RuntimeException("Failed", ex);
			}
		}
	}
	
	
}
