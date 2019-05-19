package fr.an.tests.webfluxservlerlog.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import fr.an.tests.webfluxservlerlog.svc.RecentLogService;
import fr.an.tests.webfluxservlerlog.ws.LogEventDTO;

public class LogServiceAppender extends AppenderBase<ILoggingEvent> {

	private RecentLogService target;	


	public LogServiceAppender() {
	}

	public LogServiceAppender(RecentLogService target) {
		this.target = target;
	}

	public void setTarget(RecentLogService target) {
		this.target = target;
	}

	@Override
	protected void append(ILoggingEvent src) {
		String traceId = null; // TODO
		String userName = null;
		String traceRequest = null;

		LogEventDTO eventDTO = new LogEventDTO(
				src.getThreadName(),
				src.getTimeStamp(),
				src.getLoggerName(),
				src.getLevel().toString(),
				src.getFormattedMessage(),
				(src.getThrowableProxy() != null)? src.getThrowableProxy().getMessage() : null, // TODO stack trace..
				src.getMessage(),
				src.getArgumentArray(),
				traceId,
				userName, 
				traceRequest);

		target.append(eventDTO);
	}

}
