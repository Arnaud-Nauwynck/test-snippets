package fr.an.tests.k8s.model;

import org.jsonschema2pojo.AbstractRuleLogger;
import org.slf4j.Logger;

public class SimpleSlf4jRuleLogger extends AbstractRuleLogger {
	private Logger log;
	
	public SimpleSlf4jRuleLogger(Logger log) {
		this.log = log;
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return log.isTraceEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	protected void doWarn(String msg, Throwable e) {
		log.warn(msg, e);
	}

	@Override
	protected void doTrace(String msg) {
		log.trace(msg);
	}

	@Override
	protected void doInfo(String msg) {
		log.info(msg);
	}

	@Override
	protected void doError(String msg, Throwable e) {
		log.error(msg, e);		
	}

	@Override
	protected void doDebug(String msg) {
		log.debug(msg);
	}
}