package fr.an.test.serverpool;

public class RemoteWorkerExecutionException extends RuntimeException {
	
	/** */
	private static final long serialVersionUID = 1L;

	private int durationMillis;
	
	// TOADD??
//	private String processDataUserName;
//	private String processDataParam;
	
	private String serverMessage;
	private String serverStackString;

	// ------------------------------------------------------------------------
	
	public RemoteWorkerExecutionException(
			String msg, Exception cause,
			int durationMillis, String serverMessage, String serverStackString) {
		super(msg, cause);
		this.durationMillis = durationMillis;
		this.serverMessage = serverMessage;
		this.serverStackString = serverStackString;
	}
	
	// ------------------------------------------------------------------------

	public int getDurationMillis() {
		return durationMillis;
	}

	public void setDurationMillis(int durationMillis) {
		this.durationMillis = durationMillis;
	}

	public String getServerMessage() {
		return serverMessage;
	}

	public void setServerMessage(String serverMessage) {
		this.serverMessage = serverMessage;
	}

	public String getServerStackString() {
		return serverStackString;
	}

	public void setServerStackString(String serverStackString) {
		this.serverStackString = serverStackString;
	}

	// ------------------------------------------------------------------------
	
	
}
