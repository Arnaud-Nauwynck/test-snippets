package fr.an.tests.projectreactor.helper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingRequestMoreSubscriber<T> implements Subscriber<T> {
	
	private final String name;
	private Subscription subscr;
	
	public LoggingRequestMoreSubscriber(String name) {
		this.name = name;
	}

	@Override
	public void onSubscribe(Subscription subscr) {
		this.subscr = subscr;
		log.info("## " + name + " onSubscribe " + subscr + " .. request(1)");
		subscr.request(1);
	}

	@Override
	public void onNext(T elt) {
		log.info("## " + name + "  onNext " + elt + " .. request(1)");
		subscr.request(1);
	}

	@Override
	public void onComplete() {
		log.info("##  " + name + " onComplete");
	}

	@Override
	public void onError(Throwable ex) {
		log.info("##  " + name + " onError", ex);
	}
	
}