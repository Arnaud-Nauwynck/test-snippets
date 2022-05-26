package fr.an.hadoop.fs.dirserver.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter 
public class LoggingCallStats {
	
	private final String displayMessage;
	
	private final String name;
	
	@Setter
	private int logFreq;
	
	protected int count;
	protected long totalMillis;

	protected int countFailed;
	protected long totalMillisFailed;

	protected int logModulo = 1;

	// ------------------------------------------------------------------------
	
	public LoggingCallStats(String displayMessage, String name, int logFreq) {
		this.displayMessage = displayMessage;
		this.name = name;
		this.logFreq = logFreq;
	}
	
	// ------------------------------------------------------------------------
	
	public void incrLog(long millis) {
		this.count++;
		logModulo--;
		if (logModulo <= 0) {
			logModulo = logFreq;
			long avgMillis = totalMillis / ((count > 0)? count : 1);
			log.info(displayMessage + " count:" + count + " avg:" + avgMillis + " ms");
		}
	}

	public void incrLogFailed(long millis, Exception ex) {
		this.countFailed++;
		logModulo--;
		long avgMillis = totalMillis / ((countFailed > 0)? countFailed : 1);
		log.error(displayMessage + " countFailed:" + countFailed + " avg:" + avgMillis + " ms " + ex.getMessage());
	}

}