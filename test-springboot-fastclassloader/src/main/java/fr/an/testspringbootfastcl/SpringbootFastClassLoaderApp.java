package fr.an.testspringbootfastcl;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import fr.an.testspringbootfastcl.classloader.BinaryIndexedURLClassLoader;
import fr.an.testspringbootfastcl.classloader.ClassLoaderPreloadMode;

/**
 * 
 */

@SuppressWarnings("restriction")
public class SpringbootFastClassLoaderApp {

	public static void main(String[] args) {
		run(args);
		System.exit(0);
	}

	private static void run(String[] args) {
        ClassLoader sysClassLoader = Thread.currentThread().getContextClassLoader();

        String classLoaderClass = System.getProperty("classLoaderClass",
        		"BinaryIndexedURLClassLoader"
//        		"default"
        		);
        		
    	boolean preloadClasses = Boolean.valueOf(System.getProperty("preloadClasses", "true"));
        // test for jvm args "-verbose:class"
        boolean jvmVerboseClassArg = ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-verbose:class");
        boolean enableAudit = false; 

        
        if (sysClassLoader.getClass().getName().equals("org.springframework.boot.loader.LaunchedURLClassLoader")) {
            // do not activate in spring fat-jar mode ... only in development mode!
    	    classLoaderClass = "default"; 
    	}
    	
        if (jvmVerboseClassArg) {
            preloadClasses = false;
        }
        
    	URL[] urls = null;
    	if (sysClassLoader instanceof URLClassLoader) {
    	    urls = ((URLClassLoader) sysClassLoader).getURLs();
    	} else {
    		classLoaderClass = "default";
    	}

    	ClassLoaderPreloadMode preloadMode =
    			ClassLoaderPreloadMode.PRELOAD_BINARY_CLASS_BLOCK;
//                ClassLoaderPreloadMode.PRELOAD_BINARY_CLASS_ASYNC;

    	switch(classLoaderClass) {
    	case "BinaryIndexedURLClassLoader": {
            sun.misc.URLClassPath ucp = new sun.misc.URLClassPath(urls);
            File logDir = new File("log");
            if (!logDir.exists()) {
            	logDir.mkdirs();
            }
            File preloadBinaryFile = new File(logDir, "preload-BinaryIndexedURLClassLoader.data");
            if (! preloadBinaryFile.exists()) {
                preloadMode = ClassLoaderPreloadMode.RECORD_BINARY_CLASS_PRELOAD;
                System.out.println("preload binary file '" + preloadBinaryFile + "' not found => using record mode");
            }
            List<String> prefixesURLNotInJar = Arrays.asList("file://" + new File("target/classes").getAbsolutePath().replace("\\", "/"));
            List<String> prefixesClassnameNotInJar = Arrays.asList();
            
            BinaryIndexedURLClassLoader newClassLoader = new BinaryIndexedURLClassLoader(
                    sysClassLoader.getParent(), ucp, 
                    prefixesClassnameNotInJar,
                    prefixesURLNotInJar,
                    preloadBinaryFile, null);

    		Thread.currentThread().setContextClassLoader(newClassLoader);
    		
    		newClassLoader.preloaderInit(preloadMode);
    		
    		invokeRun(newClassLoader, args);
    		
    		newClassLoader.preloaderEnd();
    	} break;

    	case "default":
    	default:
    		doRun(args);
    		break;

    	}
    	

	}
	
	
	private static void doRun(String[] args) {
		fr.an.testspringbootfastcl.SprinbootFastClassLoaderRunner.run(args);
	}
	
	/** similar to doRun(args), but by introspection using ClassLoader */
	private static void invokeRun(ClassLoader cl, String[] args) {
		try {
			Class<?> runnerClass = cl.loadClass("fr.an.testspringbootfastcl.SprinbootFastClassLoaderRunner");
			Method runMethod = runnerClass.getMethod("run", String[].class);
			runMethod.invoke(null, new Object[] { args });
		} catch(Exception ex) {
			throw new RuntimeException("Failed to invoke SprinbootFastClassLoaderRunner.run(args)", ex);
		}
	}

	private static void checkSame(ClassLoader sysClassLoader, ClassLoader newClassLoader, String... resourceNames) {
		for(String resourceName : resourceNames) {
			checkSame(sysClassLoader, newClassLoader, resourceName);
		}
	}
	
	private static void checkSame(ClassLoader sysClassLoader, ClassLoader newClassLoader, String resourceName) {
		checkSameResource(sysClassLoader, newClassLoader, resourceName);
		checkSameResources(sysClassLoader, newClassLoader, resourceName);
	}
	
	private static void checkSameResource(ClassLoader sysClassLoader, ClassLoader newClassLoader, String resourceName) {
		URL resource = sysClassLoader.getResource(resourceName);
		URL indexedResource = newClassLoader.getResource(resourceName);
		if (! Objects.equals(resource, indexedResource)) {
			System.out.println("getResource('" + resourceName + "') => '" + resource + "' <> '" + indexedResource + "'");
		}
	}
	
	private static void checkSameResources(ClassLoader sysClassLoader, ClassLoader newClassLoader, String resourceName) {
		try {
			String resources = urlsToString(sysClassLoader.getResources(resourceName));
			String indexedResources = urlsToString(newClassLoader.getResources(resourceName));
			if (! resources.equals(indexedResources)) {
				System.out.println("getResources('" + resourceName + "') => '" + resources + "' <> '" + indexedResources + "'");
			}
		} catch(IOException ex) {
			throw new RuntimeException("Failed ", ex);
		}
	}
	
	private static String urlsToString(Enumeration<URL> urls) {
		StringBuilder sb = new StringBuilder();
		while(urls.hasMoreElements()) {
			sb.append(urls.nextElement());
			if (urls.hasMoreElements()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
