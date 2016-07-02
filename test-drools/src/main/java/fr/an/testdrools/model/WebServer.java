package fr.an.testdrools.model;

public class WebServer {

	private String id;
	private String tag;

	// ------------------------------------------------------------------------

	public WebServer() {
	}

	
	public WebServer(String id, String tag) {
		this.id = id;
		this.tag = tag;
	}

	// ------------------------------------------------------------------------

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return "WebServer[" + id + "]";
	}
	
}
