package fr.an.tests.webfluxservlerlog.ws;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/bar")
@Slf4j

@CrossOrigin(origins = "http://localhost:4200") // TODO

public class BarController {

	private int barIdGenerator = 1;

	@GetMapping("hello")
	public void hello() {
		log.info("bar hello");
	}

	@GetMapping("repeatHello")
	public void repeatHello() {
		log.info("bar repeatHello..");
		int count = 10;
		for (int i = 0; i < count; i++) {
			log.info("bar hello " + barIdGenerator++ + " (" + i + "/" + count + ")");
			try {
				Thread.sleep(200);
			} catch (Exception ex) {
			}
		}
		log.info(".. done bar repeatHello");
	}

	@GetMapping("repeatFastHello")
	public void repeatFastHello() {
		log.info("bar repeatFastHello");
		int count = 10;
		for (int i = 0; i < count; i++) {
			log.info("bar repeatFastHello " + barIdGenerator++ + " (" + i + "/" + count + ")");
			try {
				Thread.sleep(50);
			} catch (Exception ex) {
			}
		}
		log.info(".. done bar repeatFastHello");
	}

	@GetMapping("repeatSlowHello")
	public void repeatSlowHello() {
		log.info("bar repeatSlowHello");
		int count = 10;
		for (int i = 0; i < count; i++) {
			log.info("bar repeatSlowHello " + barIdGenerator++ + " (" + i + "/" + count + ")");
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {
			}
		}
		log.info(".. done bar repeatSlowHello");
	}

}
