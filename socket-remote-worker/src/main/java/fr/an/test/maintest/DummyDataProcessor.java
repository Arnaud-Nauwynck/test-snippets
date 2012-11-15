package fr.an.test.maintest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.an.test.serverpool.DataProcessor;

public class DummyDataProcessor extends DataProcessor {

	
	private static Logger log = LoggerFactory
			.getLogger(DummyDataProcessor.class);
	
	// ------------------------------------------------------------------------

	public DummyDataProcessor() {
	}
	
	// ------------------------------------------------------------------------

	public boolean isClosed() {
		return false;
	}

	public boolean pingAlive() {
		return true;
	}

	@Override
	public String processData(String userName, String data) {
		log.info("processData userName:" + userName + ", data:" + data);
		if (data.startsWith("wait=")) {
			data = data.substring(5);
			int waitMillis = 1000 * Integer.parseInt(data);
			log.info("Thread.sleep...");
			try {
				Thread.sleep(waitMillis);
			} catch(InterruptedException ex) {
				// do nothing
			}
			log.info(".. done Thread.sleep");
		}
		return "dummy processData => OK, echo data='" + data + "'";
	}
	
}
