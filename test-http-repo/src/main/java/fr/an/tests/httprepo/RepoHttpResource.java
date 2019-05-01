package fr.an.tests.httprepo;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepoHttpResource {

	File repoBaseDir;

	public RepoHttpResource(
		@Value("${repo.basedir}") String repoBaseDir) {
		this.repoBaseDir = new File(repoBaseDir);
	}
	
	@GetMapping(path = "/repo/**", 
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<? extends Resource> handle(
			HttpServletRequest request
			) throws MalformedURLException {
		String requestURI = request.getRequestURI();
		String path = requestURI.toString().substring("/repo/".length());
		File file = new File(repoBaseDir, path);
		if (file.exists()) {
			return ResponseEntity.ok(new FileUrlResource(file.toURI().toURL()));
		} else {
			return ResponseEntity
					.status(200) // should be 300 in a redirect url...
					.body(new ClassPathResource("static/dummy-page.html"));
		}
	}
	
}
