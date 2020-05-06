package fr.an.tests.opentelemetry;

import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenTelemetryAppMain2 {
	
	public static void main(String[] args) {
		OpenTelemetryAppMain2 app = new OpenTelemetryAppMain2();
		app.run();
	}

	private void run() {
		CustomLogSpanProcessor customLogSpanProcessor = new CustomLogSpanProcessor();
		OpenTelemetrySdk.getTracerProvider().addSpanProcessor(customLogSpanProcessor);
				
		InstrumentedFoo ifoo = new InstrumentedFoo();
		
		ifoo.startEndSpan();
		log.info("\n");
		
		ifoo.startEndSpan_attach();
		log.info("\n");

		ifoo.foo();
		log.info("\n");

	}

	@Slf4j
	public static class CustomLogSpanProcessor implements SpanProcessor {

		@Override
		public void onStart(ReadableSpan span) {
			log.info("## onStart " + span.getName());
			
		}

		@Override
		public void onEnd(ReadableSpan span) {
			log.info("## onEnd " + span.getName());
			
		}

		@Override
		public boolean isStartRequired() {
			return true;
		}

		@Override
		public boolean isEndRequired() {
			return true;
		}

		@Override
		public void shutdown() {
		}

		@Override
		public void forceFlush() {
		}
		
	}
}
