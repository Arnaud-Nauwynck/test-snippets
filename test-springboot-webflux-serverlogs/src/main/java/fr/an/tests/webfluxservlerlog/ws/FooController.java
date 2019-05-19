package fr.an.tests.webfluxservlerlog.ws;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/foo")
@Slf4j
public class FooController {

	private int fooIdGenerator = 1;

	@GetMapping("hello")
	public void hello() {
		log.info("hello");
	}

	@GetMapping("repeatHello")
	public void repeatHello() {
		for (int i = 0; i < 10; i++) {
			log.info("hello " + fooIdGenerator++);
			try {
				Thread.sleep(1000);
			} catch (Exception ex) {
			}
		}
	}

	@GetMapping("repeatFastHello")
	public void repeatFastHello() {
		for (int i = 0; i < 30; i++) {
			log.info("hello " + fooIdGenerator++);
		}
	}

	@GetMapping("repeatSlowHello")
	public void repeatSlowHello() {
		for (int i = 0; i < 30; i++) {
			log.info("slow hello " + fooIdGenerator++);
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {
			}
		}
	}

}
