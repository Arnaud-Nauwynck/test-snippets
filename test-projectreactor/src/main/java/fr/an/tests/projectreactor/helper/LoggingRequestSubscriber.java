package fr.an.tests.projectreactor.helper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingRequestSubscriber<T> implements Subscriber<T> {
	
	private final String name;
	private final int request;
	
	public LoggingRequestSubscriber(String name, int request) {
		this.name = name;
		this.request = request;
	}

	@Override
	public void onSubscribe(Subscription subscr) {
		log.info("## " + name + " onSubscribe " + subscr);
		subscr.request(request);
	}

	@Override
	public void onNext(T elt) {
		log.info("## " + name + "  onNext " + elt);
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