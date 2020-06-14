package fr.an.tests.projectreactor;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.junit.Test;
import org.reactivestreams.Publisher;

import com.sun.net.httpserver.Authenticator.Retry;

import fr.an.tests.projectreactor.helper.BuggedNoRequestLoggingSubscriber;
import fr.an.tests.projectreactor.helper.LoggingConsumer;
import fr.an.tests.projectreactor.helper.LoggingRequestMoreSubscriber;
import fr.an.tests.projectreactor.helper.LoggingRequestSubscriber;
import fr.an.tests.projectreactor.helper.MyBaseSubscriber;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class ProjectReactorTest {

	@Test
	public void testFlux() {
		Flux<String> seq1 = Flux.just("foo", "bar", "foobar");
		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(loggingSubscriber)");
		seq1.subscribe(new BuggedNoRequestLoggingSubscriber<String>("test1"));
		// => only 1 onSubscribe ... no onNext ... need to use subscription.request(..) !!

		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(loggingRequestSubscriber(2))");
		seq1.subscribe(new LoggingRequestSubscriber<String>("test1", 2));
		// => receive 2x onNext ... (no onComplete)
		
		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(loggingRequestModeSubscriber())");
		seq1.subscribe(new LoggingRequestMoreSubscriber<String>("test1"));
		// => ok ! receive all + onComplete

		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(BaseSubscriber())");
		seq1.subscribe(new MyBaseSubscriber<String>("test1"));
		// => ok ! receive all ..

		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(loggingConsumer)");
		seq1.subscribe(new LoggingConsumer<String>("testConsumer"));

		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(lambda)");
		seq1.subscribe(x -> { log.info("testSubscribeLambda " + x); });

		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(lambda3)");
		seq1.subscribe(
				x -> { log.info("testSubscribe3Lambda " + x); },
				err -> { log.error("testSubscribe3Lambda error", err);},
				() -> { log.info("testSubscribe3Lambda complete"); });

		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(lambda4)  ... call only onSubscribe !!!");
		seq1.subscribe(
				x -> { log.info("testSubscribe4Lambda " + x); },
				err -> { log.error("testSubscribe4Lambda error", err);},
				() -> { log.info("testSubscribe4Lambda complete"); },
				subscr -> { log.info("testSubscribe4Lambda onSubscribe " + subscr); });

		log.info("Flux.just('foo', 'bar', 'foobar').subscribe(lambda4) + request(4)  ... ok");
		seq1.subscribe(
				x -> { log.info("testSubscribe4Lambda " + x); },
				err -> { log.error("testSubscribe4Lambda error", err);},
				() -> { log.info("testSubscribe4Lambda complete"); },
				subscr -> { log.info("testSubscribe4Lambda onSubscribe " + subscr); 
					subscr.request(4);
				});

//		seq1.subscribe(new LoggingConsumer<String>("test1"));

		// Iterable<String> iterable = Arrays.asList("foo", "bar", "foobar");
		// Flux<String> seq2 = Flux.fromIterable(iterable);
		
		log.info("Flux.range(1, 3).subscribe(print)");
		Flux<Integer> ints1_3 = Flux.range(1, 3); 
		ints1_3.subscribe(i -> System.out.println(i)); 

		// test map()
		log.info("Flux.just(foo,bar,foobar).map(..).subscribe(subscriber)  ... map function is NOT CALLED !!");
		seq1.map(x -> mapNotCalled(x)).subscribe(new BuggedNoRequestLoggingSubscriber<String>("test1"));

		log.info("Flux.just(foo,bar,foobar).map(..).subscribe(consumer)");
		seq1.map(x -> "map" + x).subscribe(new LoggingConsumer<String>("test1"));

		// test map(.. error for 2nd)
		log.info("Flux.just(foo,bar,foobar).map(..error for 2nd).subscribe(subscriber)  ... map function is NOT CALLED !!");
		seq1.map(x -> mapNotCalled(x)).subscribe(new BuggedNoRequestLoggingSubscriber<String>("test1"));

		log.info("Flux.just(foo,bar,foobar).map(..).subscribe(consumer) + try-catch");
		try {
			seq1.map(x -> mapErrorBar(x)).subscribe(new LoggingConsumer<String>("test1"));
		} catch(Exception ex) {
			log.warn("Error not captured.. " + ex.getMessage());
		}

		log.info("Flux.just(foo,bar,foobar).map(..).subscribe(consumer, errConsumer)");
		seq1.map(x -> mapErrorBar(x)).subscribe(
				new LoggingConsumer<String>("test1"),
				ex -> log.info("OK, captured error " + ex.getMessage()));

	}
	
	@Test
	public void testGenerate() {
		log.info("testGenerate '3 x i = ..'  (and dump state i on complete)");
		Flux<String> flux = Flux.generate(AtomicLong::new, (state, sink) -> {
			long i = state.getAndIncrement();
			sink.next("3 x " + i + " = " + 3 * i);
			if (i == 4)
				sink.complete();
			return state;
		}, (state) -> System.out.println("state: " + state));
		flux.subscribe(new LoggingRequestMoreSubscriber<String>("testGenerate"));
	}

	@Test
	public void testMono() {
		log.info("Mono.just('foo').subscribe(loggingSubscriber)");
		Mono<String> seq1 = Mono.just("foo");
		seq1.subscribe(new LoggingRequestSubscriber<String>("test2", 1));

		log.info("Mono.empty().subscribe(loggingSubscriber)");
		Mono.<String>empty().subscribe(new LoggingRequestSubscriber<String>("test3", 1));
		

	}


	@Test
	public void testThreadPublishOn() {
		Scheduler publicScheduler = Schedulers.newParallel("publish-scheduler", 2); 

		final Flux<String> flux = Flux
		    .range(1, 2)
		    .map(i -> { // executed in thread "MyThread"
		    	log.info("map i:" + i + " -> i+10");
		    	return 10 + i;	
		    })  
		    .publishOn(publicScheduler)  
		    .map(x -> { // executed in thread "publish-scheduler-1"
		    	log.info("map x:" + x + "->'value '+x");
		    	return "value " + x;  
		    });

		new Thread(() -> {
			flux.subscribe(x -> { // execute in thread "publish-scheduler-1"
				log.info("onNext " + x);
			});
		}, "MyThread").start();
		
		sleep(1000);
		publicScheduler.dispose();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	@Test
	public void testThreadSubscribeOn() {
		Scheduler publishScheduler = Schedulers.newParallel("publish-scheduler", 2); 
		Scheduler subscribeScheduler = Schedulers.newParallel("subscribe-scheduler", 2); 

		final Flux<String> flux = Flux
		    .range(1, 2)
		    .map(i -> { // executed in thread "subscribe-scheduler-2"
		    	log.info("map i:" + i + " -> i+10");
		    	return 10 + i;	
		    })  
		    .publishOn(publishScheduler)
		    .subscribeOn(subscribeScheduler)
		    .map(x -> { // executed in thread "publish-scheduler-3"
		    	log.info("map x:" + x + "->'value '+x");
		    	return "value " + x;  
		    });

		new Thread(() -> {
			flux.subscribe(x -> { // execute in thread "publish-scheduler-3"
				log.info("onNext " + x);
			});
		}, "MyThread").start();  		

		sleep(1000);
		publishScheduler.dispose();
		subscribeScheduler.dispose();
	}
	
	@Test
	public void testDoOnSubscribe_DoFinally() {
		Flux.just("foo", "bar")
		    .doOnSubscribe(subscription -> {
		    	log.info("doOnSubscribe");
		    })
		    .doFinally(type -> { 
		    	log.info("doFinally");
		    })
		    .subscribe(x -> {
		    	log.info("onNext " + x);
		    }); 
	}
	
	@Test
	public void testUsing() {
		AtomicBoolean isDisposed = new AtomicBoolean();
		Disposable disposableInstance = new Disposable() {
		    @Override
		    public void dispose() {
		        isDisposed.set(true); 
		    }

		    @Override
		    public String toString() {
		        return "DISPOSABLE";
		    }
		};
		Flux<String> flux =
				Flux.using(
				        () -> disposableInstance, 
				        disposable -> Flux.just(disposable.toString()), 
				        Disposable::dispose // called even if subscription cancel
				);
		flux.subscribe(x -> {
			log.info("testUsing onNext " + x);
		});
	}
	
	@Test
	public void testOnErrorReturn() {
		Flux<String> flux = Flux.interval(Duration.ofMillis(10)).map(input -> {
			if (input < 3) {
				return "tick " + input;
			}
			throw new RuntimeException("boom");
		}).onErrorReturn("Uh oh");

		flux.subscribe(x -> {
			log.info("testOnErrorReturn " + x);
		});
		sleep(1000);
	}
	
	@Test
	public void testRetry() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		Flux.interval(Duration.ofMillis(50))
	    .map(input -> {
	        if (input < 3) return "tick " + input;
	        throw new RuntimeException("boom");
	    })
	    .retry(2)
	    .elapsed() 
	    .doFinally(x -> { latch.countDown(); })
	    .subscribe(System.out::println, System.err::println); 

		latch.await();
	}
	
	@Test
	public void testRetryWhen() {
		Flux<String> flux = Flux.<String>error(new IllegalArgumentException()) 
			    .doOnError(System.out::println) 
			    .retryWhen(when -> when.take(3));
		flux
		.subscribe(x -> {
			log.info("onNext " + x);
		}, err -> {
			log.info("onError " + err);
		}, () -> {
			log.info("onComplete ");
		});
	}
	
	
	private static String mapNotCalled(String x) {
		log.error("##### unexpected call?!!");
		return null;
	}
	
	private static String mapErrorBar(String x) {
		if (x.equals("bar")) {
			throw new RuntimeException("no map for 'foo'");	
		} else {
			return "map" + x;
		}
	}
	
}
