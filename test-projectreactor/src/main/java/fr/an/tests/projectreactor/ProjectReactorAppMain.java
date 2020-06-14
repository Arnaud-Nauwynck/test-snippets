package fr.an.tests.projectreactor;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class ProjectReactorAppMain {

	public static void main(String[] args) {
		Flux.just("foo", "bar", "foobar")
			.subscribe(x -> {
				log.info("onNext " + x);
			});
	}
}
