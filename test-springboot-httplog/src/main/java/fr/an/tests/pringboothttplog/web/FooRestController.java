package fr.an.tests.pringboothttplog.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import fr.an.tests.pringboothttplog.annotation.NoLog;
import fr.an.tests.pringboothttplog.dto.FooRequest;
import fr.an.tests.pringboothttplog.dto.FooResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Dummy testing purpose Rest Controller
 */
@RestController
@RequestMapping(path="/api/v1/foo")
@Slf4j
public class FooRestController {

	@GetMapping("/getFoo")
	public FooResponse getFoo() {
		log.info("getFoo()");
		return new FooResponse("hello", 123);
	}

	@GetMapping("/getFoos")
	public List<FooResponse> getFoos() {
		log.info("getFoos()");
		return Arrays.asList(new FooResponse("hello", 123), new FooResponse("world", 234));
	}

	@PostMapping("/postFoo")
	public FooResponse postFoo(@RequestBody FooRequest req) {
		log.info("postFoo()");
		return new FooResponse(req.strValue, req.intValue);
	}

	@PutMapping("/putFoo")
	public FooResponse putFoo(@RequestBody FooRequest req) {
		log.info("putFoo()");
		return new FooResponse(req.strValue, req.intValue);
	}

	@GetMapping("/getFooRuntimeException")
	public FooResponse getFooException() {
		log.info("getFooRuntimeException()");
		throw new RuntimeException("Failed getFooException()", new RuntimeException("nested-exception"));
	}

	@GetMapping("/getFooCheckedException")
	public FooResponse getFooCheckedException() throws Exception {
		log.info("getFooCheckedException()");
		throw new Exception("Failed getFooCheckedException()", new RuntimeException("nested-exception"));
	}

	@PostMapping("/putFooRuntimeException")
	public FooResponse putFooRuntimeException(@RequestBody FooRequest req) {
		log.info("putFooException()");
		throw new RuntimeException("Failed putFooRuntimeException(" + req.strValue + ")", new RuntimeException("nested-exception"));
	}

	@PostMapping("/putFooCheckedException")
	public FooResponse putFooCheckedException(@RequestBody FooRequest req) throws Exception {
		log.info("putFooException()");
		throw new Exception("Failed putFooCheckedException(" + req.strValue + ")", new RuntimeException("nested-exception"));
	}

	
	@NoLog
	@GetMapping("/getFooNoLog")
	public FooResponse getFooNoLog() {
		log.info("getFoo()");
		return new FooResponse("hello", 123);
	}

	
	
	@GetMapping("/getFooSlow")
	public FooResponse getFooSlow() {
		log.info("getFooSlow");
		int max = 10;
		for(int i = 0; i < max; i++) {
			sleep(1000);
			log.info("getFooSlow " + i + "/" + max);
		}
		log.info(".. done getFooSlow");
		return new FooResponse("hello", 123);
	}

	@GetMapping("/getFooDeferred")
    public DeferredResult<FooResponse> getFooDeferred() {
        DeferredResult<FooResponse> df = new DeferredResult<>();
        df.setResult(new FooResponse("hello", 123));
        return df;
    }
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		}  catch(Exception ex) {
		}
	}

}
