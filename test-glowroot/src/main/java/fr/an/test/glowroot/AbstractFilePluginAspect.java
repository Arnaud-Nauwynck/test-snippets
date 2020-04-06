package fr.an.test.glowroot;

import org.glowroot.agent.plugin.api.config.ConfigService;

public class AbstractFilePluginAspect {

	private static boolean showStackTrace;
	private static boolean formatStackTraceSingleLine;
	static {
		System.out.println("#### (glowroot-file)  cinit class fr.an.test.glowroot.FileAspect");
		ConfigService serviceConfig = org.glowroot.agent.plugin.api.Agent.getConfigService("file"); // cf value in META-INF/glowroot.plugin.json
		
		showStackTrace = serviceConfig.getBooleanProperty("showStackTrace").value();
		formatStackTraceSingleLine = serviceConfig.getBooleanProperty("formatStackTraceSingleLine").value();
	}


    protected static void logCall(String msg) {
		System.out.println("#### (glowroot-file) " + msg);
		if (showStackTrace) {
			if (formatStackTraceSingleLine) {
				System.out.println("#### (glowroot-file) from stack: " + ExUtils.currentStackTraceShortPath());
			} else {
				System.out.println("#### (glowroot-file) from stack: (not an exception)");
				new Exception().printStackTrace();
			}
		}
    	
    }

}
