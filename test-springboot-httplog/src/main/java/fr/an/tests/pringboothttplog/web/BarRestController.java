package fr.an.tests.pringboothttplog.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.pringboothttplog.annotation.NoLog;
import fr.an.tests.pringboothttplog.dto.BarRequest;
import fr.an.tests.pringboothttplog.dto.BarResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Dummy testing purpose Rest Controller
 */
@RestController
@RequestMapping(path="/api/v1/bar")
@Slf4j
public class BarRestController {

	@GetMapping("/getBar")
	public BarResponse getBar() {
		log.info("getBar()");
		return new BarResponse("hello", 123);
	}

	@GetMapping("/getBars")
	public List<BarResponse> getBars() {
		log.info("getBars()");
		return Arrays.asList(new BarResponse("hello", 123), new BarResponse("world", 234));
	}

	@PostMapping("/postBar")
	public BarResponse postBar(@RequestBody BarRequest req) {
		log.info("postBar()");
		return new BarResponse(req.strValue, req.intValue);
	}

	@PutMapping("/putBar")
	public BarResponse putBar(@RequestBody BarRequest req) {
		log.info("putBar()");
		return new BarResponse(req.strValue, req.intValue);
	}

	@GetMapping("/getBarRuntimeException")
	public BarResponse getBarException() {
		log.info("getBarRuntimeException()");
		throw new RuntimeException("Failed getBarException()", new RuntimeException("nested-exception"));
	}

	@GetMapping("/getBarCheckedException")
	public BarResponse getBarCheckedException() throws Exception {
		log.info("getBarCheckedException()");
		throw new Exception("Failed getBarCheckedException()", new RuntimeException("nested-exception"));
	}

	@PostMapping("/putBarRuntimeException")
	public BarResponse putBarRuntimeException(@RequestBody BarRequest req) {
		log.info("putBarException()");
		throw new RuntimeException("Failed putBarRuntimeException(" + req.strValue + ")", new RuntimeException("nested-exception"));
	}

	@PostMapping("/putBarCheckedException")
	public BarResponse putBarCheckedException(@RequestBody BarRequest req) throws Exception {
		log.info("putBarException()");
		throw new Exception("Failed putBarCheckedException(" + req.strValue + ")", new RuntimeException("nested-exception"));
	}

	
	@NoLog
	@GetMapping("/getBarNoLog")
	public BarResponse getBarNoLog() {
		log.info("getBar()");
		return new BarResponse("hello", 123);
	}

}
