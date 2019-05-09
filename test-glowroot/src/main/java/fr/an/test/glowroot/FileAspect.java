package fr.an.test.glowroot;

import java.io.File;

import org.glowroot.agent.plugin.api.config.ConfigService;
import org.glowroot.agent.plugin.api.weaving.BindParameter;
import org.glowroot.agent.plugin.api.weaving.BindReceiver;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.Pointcut;

public class FileAspect {

	private static ConfigService serviceConfig;
	private static String rootWritePath;
	private static String writeSuffix;
	static {
		System.out.println("**** cinit class fr.an.test.glowroot.FileAspect");
		serviceConfig = org.glowroot.agent.plugin.api.Agent.getConfigService("file"); // cf value in META-INF/glowroot.plugin.json
		rootWritePath = serviceConfig.getStringProperty("writeRootPath").value();
		writeSuffix = serviceConfig.getStringProperty("writeSuffix").value();
	}
	
	static int writeCount = 0;
	static int writeCountTotal = 0;
	static int fileRenameCount = 0;
	static int fileRenameCountTotal = 0;
	
	
//    @Shim("java.io.FileOutputStream")
//    public interface FileOutputStreamShim {
//        String name();
//    }

    @Pointcut(className = "java.io.FileOutputStream", methodName = "open", methodParameterTypes = {"java.lang.String", "boolean"})
    public static class FileOutputStreamOpenAdvice {

        @OnBefore
        public static void onBefore(
        		// @BindParameter FileOutputStreamOpenShim thisShim,
        		@BindParameter String path
        		) {
        	writeCountTotal++;
    		// System.out.println("***** open write '" + path + "' ... ");
        	if (path.startsWith(rootWritePath) && path.endsWith(writeSuffix)) {
        		writeCount++;
        		System.out.println("***** (" + writeCount + "/" + writeCountTotal + ") write " + path + " from stack:" + ExUtils.currentStackTraceShortPath());
        	}
        }

    }
    
    
//    @Shim("java.io.File")
//    public interface FileShim {
//    }

    @Pointcut(className = "java.io.File", methodName = "renameTo", methodParameterTypes = {"java.lang.String"})
    public static class FileAdvice {

        @OnBefore
        public static void onBefore(
        		@BindReceiver File thisFile,
        		@BindParameter File dest) {
        	String fromPath = thisFile.getAbsolutePath();
        	String toPath = dest.getAbsolutePath();
    		// System.out.println("***** renameTo " + toPath + " ... ");
        	fileRenameCountTotal++;
        	if (toPath.startsWith(rootWritePath) && toPath.endsWith(writeSuffix)) {
        		fileRenameCount++;
        		System.out.println("***** (" + fileRenameCount + "/" + fileRenameCountTotal + ") File.renameTo '" + toPath + "' src:'" + fromPath + "' from stack:" + ExUtils.currentStackTraceShortPath());
        	}
        }

    }

}