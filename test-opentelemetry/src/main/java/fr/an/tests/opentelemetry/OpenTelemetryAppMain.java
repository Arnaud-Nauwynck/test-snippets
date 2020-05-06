package fr.an.tests.opentelemetry;

import io.opentelemetry.exporters.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.export.SimpleSpansProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenTelemetryAppMain {

	public static void main(String[] args) {
		OpenTelemetryAppMain app = new OpenTelemetryAppMain();
		app.run();
	}

	private void run() {
		LoggingSpanExporter loggingSpanExporter = new LoggingSpanExporter();
		SimpleSpansProcessor loggingProcessor = SimpleSpansProcessor.create(loggingSpanExporter);
		OpenTelemetrySdk.getTracerProvider().addSpanProcessor(loggingProcessor);
				
		InstrumentedFoo ifoo = new InstrumentedFoo();

		ifoo.startEndSpan();
		log.info("\n");
		
		ifoo.startEndSpan_attach();
		log.info("\n");

		ifoo.foo();
		log.info("\n");
		
		OpenTelemetrySdk.getTracerProvider().forceFlush();
	}

}
