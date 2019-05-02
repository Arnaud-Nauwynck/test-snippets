package fr.an.tests.httprepo;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RepoHttpResource {

	File repoBaseDir;

	Set<String> resp404;
      
	public RepoHttpResource(
		@Autowired AppConfigProperties config) {
		this.repoBaseDir = new File(config.getBaseDir());
		this.resp404 = new HashSet<>(config.getResp404());
	}
	
	@GetMapping(path = "/repo/**", 
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<? extends Resource> handleGet(
			HttpServletRequest request
			) throws MalformedURLException {
		String requestURI = request.getRequestURI();
		String path = requestURI.toString().substring("/repo/".length());
		
		if (resp404.contains(path)) {
			System.out.println("http GET .. repo path='" + path +"' .. return 404");
			return ResponseEntity.notFound().build();
		}
		
		File file = new File(repoBaseDir, path);
		if (file.exists()) {
			System.out.println("http GET .. repo path='" + path +"' .. return file content");
			return ResponseEntity.ok(new FileUrlResource(file.toURI().toURL()));
		} else {
			System.out.println("http GET .. repo path='" + path +"' .. return dummy html page");
			return ResponseEntity
					.status(200) // should be 300 in a redirect url...
					.body(new ClassPathResource("static/dummy-page.html"));
		}
	}

	@PostMapping(path = "/repo/**", 
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<?> handlePost(
			HttpServletRequest request
			) throws MalformedURLException {
		String requestURI = request.getRequestURI();
		String path = requestURI.toString().substring("/repo/".length());
		System.out.println("http POST .. repo path='" + path +"' .. return 200 no content");

		return ResponseEntity.noContent().build();
	}

	@PutMapping(path = "/repo/**", 
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<?> handlePut(
			HttpServletRequest request
			) throws MalformedURLException {
		String requestURI = request.getRequestURI();
		String path = requestURI.toString().substring("/repo/".length());
		System.out.println("http PUT .. repo path='" + path +"' return 200 no content");

		return ResponseEntity.noContent().build();
	}

}
