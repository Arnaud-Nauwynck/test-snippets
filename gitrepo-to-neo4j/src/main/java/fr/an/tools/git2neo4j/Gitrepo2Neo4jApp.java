package fr.an.tools.git2neo4j;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.an.tools.git2neo4j.service.RevCommitSyncService;

@SpringBootApplication
public class Gitrepo2Neo4jApp implements CommandLineRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(Gitrepo2Neo4jApp.class);
	
	@Autowired
	private RevCommitSyncService git2Neo4JSyncService;
	
	public static void main(String[] args) {
		try {
			SpringApplication.run(Gitrepo2Neo4jApp.class, args);
		} catch(Throwable ex) {
			ex.printStackTrace(System.err);
		}
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("http://localhost:7474/browser/");
		File localGitRepo =
				new File("/home/arnaud/downloadTools/eclipse-git/jgit/jgit.github");
				// new File(".."); //TODO test from self local repo ... 
		LOG.info("using git repo:" + localGitRepo);
		if (! new File(localGitRepo, ".git").exists()) {
			throw new RuntimeException();
		}
		
		Git git = Git.init().setDirectory(localGitRepo).call();
		
		git2Neo4JSyncService.syncRepo(git);
	}
}
