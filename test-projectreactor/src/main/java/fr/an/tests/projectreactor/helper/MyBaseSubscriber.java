package fr.an.tests.projectreactor.helper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.SignalType;

@Slf4j
public class MyBaseSubscriber<T> extends BaseSubscriber<T> {

		private final String name;
		
		public MyBaseSubscriber(String name) {
			this.name = name;
		}


		// cf .. 
//		protected void hookOnSubscribe(Subscription subscription){
//			subscription.request(Long.MAX_VALUE);
//		}

		@Override
		protected void hookOnNext(T value) {
			// NO-OP super.hookOnNext(value);
			log.info("## " + name + " onNext " + value);
		}

		@Override
		protected void hookOnComplete() {
			// NO-OP super.hookOnComplete();
			log.info("## " + name + " onComplete");
		}

		@Override
		protected void hookOnError(Throwable ex) {
			// NO-OP super.hookOnError(ex);
			log.info("## " + name + " onError " + ex.getMessage());
		}


		@Override
		protected void hookFinally(SignalType type) {
			// NO-OP super.hookFinally(type);
			log.info("## " + name + " hookFinally " + type);
		}

		
	}