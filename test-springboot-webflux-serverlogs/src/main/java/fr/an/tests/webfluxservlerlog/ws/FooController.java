package fr.an.tests.webfluxservlerlog.ws;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/foo")
@Slf4j

@CrossOrigin(origins = "http://localhost:4200") // TODO

public class FooController {

	private int fooIdGenerator = 1;

	@GetMapping("hello")
	public void hello() {
		log.info("foo hello");
	}

	@GetMapping("repeatHello")
	public void repeatHello() {
		log.info("foo repeatHello..");
		int count = 10;
		for (int i = 0; i < count; i++) {
			log.info("foo hello " + fooIdGenerator++ + " (" + i + "/" + count + ")");
			try {
				Thread.sleep(200);
			} catch (Exception ex) {
			}
		}
		log.info(".. done foo repeatHello");
	}

	@GetMapping("repeatFastHello")
	public void repeatFastHello() {
		log.info("foo repeatFastHello");
		int count = 10;
		for (int i = 0; i < count; i++) {
			log.info("foo repeatFastHello " + fooIdGenerator++ + " (" + i + "/" + count + ")");
			try {
				Thread.sleep(50);
			} catch (Exception ex) {
			}
		}
		log.info(".. done foo repeatFastHello");
	}

	@GetMapping("repeatSlowHello")
	public void repeatSlowHello() {
		log.info("foo repeatSlowHello");
		int count = 10;
		for (int i = 0; i < count; i++) {
			log.info("foo repeatSlowHello " + fooIdGenerator++ + " (" + i + "/" + count + ")");
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {
			}
		}
		log.info(".. done foo repeatSlowHello");
	}

}
