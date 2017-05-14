package fr.an.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequestMapping("/app")
public class MyRestController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MyRestController.class);
	
	/**
	 * to test...
	 * curl -X GET -H 'Accept: application/json' http://localhost:8080/app/helloParams
	 */
	@GetMapping("/helloParams")
	public Map<String,String> helloParams() {
		Map<String,String> res = new HashMap<>();
		res.put("hello", "world");
		return res;
	}

	
	public static class NamedBlob {
		public String name;
		public byte[] content;
	}
	
	/** upload using json objects .. containing byte[] */
	@PostMapping(value="/uploadDatas")
	public void uploadDatas(@RequestBody List<NamedBlob> req) {
		LOG.info("/uploadDatas");
		for(NamedBlob b : req) {
			logContentIfText(b.name, b.content);
		}
	}
	
	
	@PostMapping(value="/uploadMultipartFiles", consumes = {"multipart/form-data"})
	public void uploadMultipartFile(MultipartHttpServletRequest request) throws IOException {
		LOG.info("/uploadMultipartFiles ");
		for(Map.Entry<String, MultipartFile> e : request.getFileMap().entrySet()) {
			String fileName = e.getKey();
			byte[] content = e.getValue().getBytes();
			logContentIfText(fileName, content);
		}
	}
	
	@PostMapping(value="/uploadMultipartFile", consumes = {"multipart/form-data"})
	public void uploadMultipartFile12(@RequestPart("file") MultipartFile file) throws IOException {
		LOG.info("/uploadMultipartFile " + file.getName() + " using MultipartFile");
		logContentIfText(file.getName(), file.getBytes());
	}
	
	@PostMapping(value="/uploadMultipartFile12", consumes = {"multipart/form-data"})
	public void uploadMultipartFile12(
			@RequestPart("file1") MultipartFile file1,
			@RequestPart("file2") MultipartFile file2
			) throws IOException {
		LOG.info("/uploadMultipartFile12 " + file1.getName() + " " + file2.getName() + " using MultipartFile");
		logContentIfText(file1.getName(), file1.getBytes());
		logContentIfText(file2.getName(), file1.getBytes());
	}


	private void logContentIfText(String name, byte[] content) {
		LOG.info("'" + name + "' length: " + content.length + " byte(s)");
		if (name.endsWith(".xml") || name.endsWith(".txt")) {
			String contentStr = new String(content);
			int len = contentStr.length();
			LOG.info("content:\n" + ((len < 10)? contentStr 
					: contentStr.substring(0, 10) + ".." + contentStr.substring(len-10, len)));
		}
	}
	

	@PostMapping(value="/uploadFileStream/{fileName:.+}", consumes=MediaType.ALL_VALUE)
	public void uploadFileStream(@PathVariable(name="fileName", required=true) String fileName, 
			HttpServletRequest httpRequest) {
		int contentLength = httpRequest.getContentLength();
		LOG.info("/uploadFileStream " + fileName + " using stream, contentLength:" + contentLength);
		byte[] content;
		try {
			ServletInputStream reqInputStream = httpRequest.getInputStream();
			content = IOUtils.toByteArray(reqInputStream);
		} catch (IOException ex) {
			throw new RuntimeException("Faile dto read", ex);
		}
		logContentIfText(fileName, content);
	}

	@PostMapping(value="/uploadFileBytes/{fileName:.+}", consumes=MediaType.ALL_VALUE)
	public void uploadFileBytes(@PathVariable(name="fileName", required=true) String fileName, @RequestBody byte[] inputBody) {
		LOG.info("/uploadFile '" + fileName + "' using byte[]");
		logContentIfText(fileName, inputBody);
	}

	/** upload any data blob, without a "fileName" */
	@PostMapping(value="/uploadBytes", consumes=MediaType.ALL_VALUE)
	public void uploadData(@RequestBody byte[] inputBody) {
		LOG.info("/uploadData");
		logContentIfText("<none>", inputBody);
	}

//	@RequestMapping(value = "/downloadFile/{fileName:.+}", method = RequestMethod.GET)
//    public ResponseEntity<byte[]> downloadFile(@PathVariable(name="fileName", required=true) String fileName) {
//        try {
//            byte[] contents = ...
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.parseMediaType("application/pdf"));
//            String filename = "test.pdf";
//            headers.setContentDispositionFormData(filename, filename);
//            return new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed", e);
//        }
//    }
}
