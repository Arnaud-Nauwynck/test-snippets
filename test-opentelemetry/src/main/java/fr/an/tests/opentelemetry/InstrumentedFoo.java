package fr.an.tests.opentelemetry;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.SpanContext;
import io.opentelemetry.trace.TraceState;
import io.opentelemetry.trace.Tracer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstrumentedFoo {

	private static final Tracer TRACER = OpenTelemetry.getTracerProvider().get("test-instrumentation");

	public void startEndSpan() {
		log.info("startEndSpan ..");
		Span span = TRACER.spanBuilder("startEndSpan").startSpan();
		try {
			log.info("startEndSpan");
		} finally {
			span.end();
		}
		log.info(".. startEndSpan");
	}

	public void startEndSpan_attach() {
		log.info("startEndSpan_attach ..");
		Span span = TRACER.spanBuilder("startEndSpan_attach").startSpan();
		try {
			try (Scope scope = TRACER.withSpan(span)) {
				log.info("startEndSpan_attach");
			}
		} finally {
			span.end();
		}
		log.info(".. startEndSpan_attach");
	}

	
	public void foo() {
		log.info("foo..");
		Span span = TRACER.spanBuilder("foo")
				.startSpan();
		try {
		
			Span checkSpanNotAttached = TRACER.getCurrentSpan();
			if (checkSpanNotAttached == span) {
				log.error("unexpected..");
			}
			// need to attach it to thread local!
			try (Scope scope = TRACER.withSpan(span)) {
				Span checkSpan = TRACER.getCurrentSpan();
				if (checkSpan != span) {
					log.error("unexpected..");
				}
				
				foo2();
				
			}

		} finally {
			span.end();
		}
		log.info(".. foo");
	}
	
	private void foo2() {
		log.info("foo2 ..");
		Span span = TRACER.spanBuilder("foo2").startSpan();
		try (Scope childScope = TRACER.withSpan(span)) {

			log.info("foo2");
			
		} finally {
			span.end();
		}
		log.info(".. foo2");
	}
}
