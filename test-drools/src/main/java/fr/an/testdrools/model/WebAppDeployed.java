package fr.an.testdrools.model;

public class WebAppDeployed {

	private WebApp webApp;
	private WebServer webServer;
	
	public WebAppDeployed(WebApp webApp, WebServer webServer) {
		this.webApp = webApp;
		this.webServer = webServer;
	}

	public WebApp getWebApp() {
		return webApp;
	}

	public void setWebApp(WebApp webApp) {
		this.webApp = webApp;
	}

	public WebServer getWebServer() {
		return webServer;
	}

	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	@Override
	public String toString() {
		return "WebAppDeployed[webApp=" + webApp.getId() + ", webServer=" + webServer.getId() + "]";
	}
	
	
}
