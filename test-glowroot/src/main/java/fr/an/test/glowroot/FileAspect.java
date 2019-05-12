package fr.an.test.glowroot;

import java.io.File;
import java.util.regex.Pattern;

import org.glowroot.agent.plugin.api.config.ConfigService;
import org.glowroot.agent.plugin.api.weaving.BindParameter;
import org.glowroot.agent.plugin.api.weaving.BindReceiver;
import org.glowroot.agent.plugin.api.weaving.OnBefore;
import org.glowroot.agent.plugin.api.weaving.Pointcut;

public class FileAspect extends AbstractFilePluginAspect {

	private static String rootWritePath;
	private static Pattern writeFileNamePattern;
	static {
		ConfigService serviceConfig = org.glowroot.agent.plugin.api.Agent.getConfigService("file"); // cf value in META-INF/glowroot.plugin.json
		rootWritePath = serviceConfig.getStringProperty("writeRootPath").value();
		String writeFileNamePatternText = serviceConfig.getStringProperty("writeFilePattern").value();
		writeFileNamePattern = (writeFileNamePatternText != null && !writeFileNamePatternText.isEmpty())? 
				Pattern.compile(writeFileNamePatternText) : null;
		System.out.println("#### (glowroot-file)  logging only java.io.File modifications under path:'" + rootWritePath + "' with pattern:" + writeFileNamePatternText);
	}
	
	static int writeCount = 0;
	static int writeCountTotal = 0;
	static int fileRenameCount = 0;
	static int fileRenameCountTotal = 0;
	static int fileMkdirCount = 0;
	static int fileMkdirCountTotal = 0;
	static int fileMkdirsCount = 0;
	static int fileMkdirsCountTotal = 0;
	static int fileDeleteCount = 0;
	static int fileDeleteCountTotal = 0;
	static int fileDeleteOnExitCount = 0;
	static int fileDeleteOnExitCountTotal = 0;
	
    /**
     * Instrument calls to <code>java.io.FileOutputStream.open()</code>
     */
    @Pointcut(className = "java.io.FileOutputStream", methodName = "open", methodParameterTypes = {"java.lang.String", "boolean"})
    public static class FileOutputStreamOpenAdvice {

        @OnBefore
        public static void onBefore(@BindParameter String path) {
        	writeCountTotal++;
        	if (matchWritePath(path)) {
        		writeCount++;
        		logCall("(" + writeCount + "/" + writeCountTotal + ") FileOutputStream.open() '" + path + "'");
        	}
        }
    }
    
    /**
     * Instrument calls to <code>java.io.File.renameTo(File)</code>
     */
    @Pointcut(className = "java.io.File", methodName = "renameTo", methodParameterTypes = {"java.io.File"})
    public static class FileRenameToAdvice {

        @OnBefore
        public static void onBefore(@BindReceiver File thisFile, @BindParameter File dest) {
        	String fromPath = thisFile.getAbsolutePath();
        	String path = dest.getAbsolutePath();
        	fileRenameCountTotal++;
        	if (matchWritePath(path)) {
        		fileRenameCount++;
        		logCall("(" + fileRenameCount + "/" + fileRenameCountTotal + ") File.renameTo() '" + path + "' src:'" + fromPath + "'");
        	}
        }
    }

    
    /**
     * Instrument calls to <code>java.io.File.mkdir()</code>
     */
    @Pointcut(className = "java.io.File", methodName = "mkdir", methodParameterTypes = {})
    public static class FileMkdirAdvice {
        @OnBefore
        public static void onBefore(@BindReceiver File thisFile) {
            String path = thisFile.getAbsolutePath();
        	fileMkdirCountTotal++;
        	if (matchWritePath(path)) {
            	fileMkdirCountTotal++;
        		logCall("File.mkdir() '" + path + "'");
        	}
        }
    }

    /**
     * Instrument calls to <code>java.io.File.mkdirs()</code>
     */
    @Pointcut(className = "java.io.File", methodName = "mkdirs", methodParameterTypes = {})
    public static class FileMkdirsAdvice {
        @OnBefore
        public static void onBefore(@BindReceiver File thisFile) {
            String path = thisFile.getAbsolutePath();
        	fileMkdirsCountTotal++;
        	if (matchWritePath(path)) {
            	fileMkdirsCount++;
        		logCall("File.mkdirs() '" + path + "'");
        	}
        }
    }

    /**
     * Instrument calls to <code>java.io.File.delete()</code>
     */
    @Pointcut(className = "java.io.File", methodName = "delete", methodParameterTypes = {})
    public static class FileDeleteAdvice {

        @OnBefore
        public static void onBefore(@BindReceiver File thisFile) {
        	String path = thisFile.getAbsolutePath();
        	fileDeleteCountTotal++;
        	if (matchWritePath(path)) {
        		fileDeleteCount++;
        		logCall("(" + fileDeleteCount + "/" + fileDeleteCountTotal + ") File.delete() '" + path + "'");
        	}
        }
    }

    /**
     * Instrument calls to <code>java.io.File.deleteOnExit()</code>
     */
    @Pointcut(className = "java.io.File", methodName = "deleteOnExit", methodParameterTypes = {})
    public static class FileDeleteOnExitAdvice {

        @OnBefore
        public static void onBefore(@BindReceiver File thisFile) {
        	String path = thisFile.getAbsolutePath();
        	fileDeleteCountTotal++;
        	if (matchWritePath(path)) {
        		fileDeleteCount++;
        		logCall("(" + fileDeleteOnExitCount + "/" + fileDeleteOnExitCountTotal + ") File.deleteOnExit() '" + path + "'");
        	}
        }
    }

    static boolean matchWritePath(String path) {
    	return path.startsWith(rootWritePath) 
    			&& (writeFileNamePattern == null || writeFileNamePattern.matcher(path).matches());
    }
       
}