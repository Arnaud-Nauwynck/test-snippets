package fr.an.tests.webfluxservlerlog.svc;

import lombok.Getter;

public class AppRequestTrace {

	public static class ThreadTraceEntry {
		@Getter
		private String requestUrl;
		@Getter
		private String username;
		@Getter
		private String traceId;
	}
	
	private static final ThreadLocal<ThreadTraceEntry> threadLocal = new ThreadLocal<ThreadTraceEntry>() {
		@Override
		protected ThreadTraceEntry initialValue() {
			return new ThreadTraceEntry();
		}
	};

	public static ThreadTraceEntry curr() {
		return threadLocal.get();
	}
	
	public static String currTraceId() {
		return curr().traceId;
	}

	public static void setTraceId(String traceId) {
		curr().traceId = traceId;
	}

	public static void startRequest(String requestUrl, String username, String traceId) {
		ThreadTraceEntry e = curr();
		e.requestUrl = requestUrl;
		e.username = username;
		e.traceId = traceId;
	}
	
	public static void endRequest() {
		ThreadTraceEntry e = curr();
		e.requestUrl = null;
		e.username = null;
		e.traceId = null;
	}
	
}
