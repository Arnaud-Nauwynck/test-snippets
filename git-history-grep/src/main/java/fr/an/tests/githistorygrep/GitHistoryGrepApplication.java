package fr.an.tests.githistorygrep;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class GitHistoryGrepApplication {

	JGitGrepHelper gitGrepHelper;
	
	public static void main(String[] args) {
		new GitHistoryGrepApplication().run();
	}
	
	public void run() {
		File gitDir = new File("src/test/gitrepo1-bare");
		gitGrepHelper = new JGitGrepHelper(gitDir);
		
		grepDisplay("a=.*");
		grepDisplay("b=.*");
		grepDisplay("c=.*");
	}
	
	public void grepDisplay(String lineRegexp) {
		Pattern filePattern = Pattern.compile("file.*.txt");
		Pattern linePattern = Pattern.compile(lineRegexp);
		System.out.println("grep git history for pattern: " + lineRegexp);
		print(gitGrepHelper.grep(null,  null, filePattern, linePattern));
		
		System.out.println();
		System.out.println("grep diff git history for pattern: " + lineRegexp);
		print(gitGrepHelper.grepInDiff(null,  null, filePattern, linePattern));

		System.out.println();
		System.out.println();
	}

	public static void print(List<String> lines) {
		for(String line : lines) {
			System.out.println(line);
		}
	}
}
