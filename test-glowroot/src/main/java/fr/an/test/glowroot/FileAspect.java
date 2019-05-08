package fr.an.test.glowroot;

import org.glowroot.agent.plugin.api.*;
import org.glowroot.agent.plugin.api.weaving.*;

public class FileAspect {

    @Shim("FileOutputStreamShim")
    public interface FileOutputStreamOpenShim {
        String name();
    }

    @Pointcut(className = "java.io.FileOutputStream", methodName = "open", methodParameterTypes = {"java.lang.String", "boolean"})
    public static class FileOutputStreamOpenAdvice {

        @OnBefore
        public static void onBefore(ThreadContext context, @BindParameter FileOutputStreamOpenShim shim) {
        	String path = shim.name();
        	if (path.startsWith("/home/arnaud/")) { // .m2/repository
        		System.out.println("write " + path + " from ");
        	}
        }

    }
    
}