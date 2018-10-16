package fr.an.testspringbootfastcl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Objects;

import fr.an.testspringbootfastcl.classloader.IndexedClassLoader;
import fr.an.testspringbootfastcl.classloader.IndexedClassLoader.PreloadMode;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import sun.misc.Resource;

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
//		boolean verboseClass = ManagementFactory.getRuntimeMXBean().getInputArguments().contains("-verbose:class");
		
		ClassLoader sysClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader newClassLoader = null;

		// always create for test only.. to compare only pure springboot part
		ClassGraph classGraph = new ClassGraph();
		classGraph.addClassLoader(sysClassLoader);
		ScanResult scanRes = classGraph.scan();

		IndexedClassLoader indexedClassLoader = new IndexedClassLoader(sysClassLoader.getParent(), scanRes);
		
		// sanity checks
		checkSame(sysClassLoader, indexedClassLoader, "", "/", "fr", "fr/");
		if (sysClassLoader instanceof URLClassLoader) {
			@SuppressWarnings("resource")
			URLClassLoader sysUrlClassLoader = (URLClassLoader) sysClassLoader;
			URL[] urls = sysUrlClassLoader.getURLs();
			sun.misc.URLClassPath ucp = new sun.misc.URLClassPath(urls); 
			Resource thisResource = ucp.getResource("fr/an/testspringbootfastcl/SpringbootFastClassLoaderApp.class");
			URL codeSourceURL = thisResource.getCodeSourceURL(); // => ".../target/classes/"
			System.out.println("this class codeSourceURL:" + codeSourceURL);
		}

		
		boolean disableIndexed = 
//			true;
			Boolean.valueOf(System.getProperty("disableIndexedURLClassLoader", "false"));
		
		// TODO .. fat-jar => indexed not supported..
		
		if (!disableIndexed && sysClassLoader instanceof URLClassLoader) {
			System.out.println("using IndexedClassLoader");
			newClassLoader = indexedClassLoader;
		} else {
			System.out.println("keeping standard system ClassLoader");
		}
		
		if (newClassLoader != null) {
			Thread.currentThread().setContextClassLoader(newClassLoader);
			
			PreloadMode preloadMode =
//					PreloadMode.RECORD;
//					PreloadMode.DEFAULT;
//					PreloadMode.PRELOAD_BY_NAME_PARALLEL_BLOCK;
//					PreloadMode.PRELOAD_BY_NAME_PARALLEL;
//					PreloadMode.RECORD_BINARY_CLASS_PRELOAD;
					PreloadMode.PRELOAD_BINARY_CLASS;
			
			indexedClassLoader.preloaderInit(preloadMode);
			
			invokeRun(newClassLoader, args);
			
			indexedClassLoader.preloaderEnd();
			
			// PreloadMode.RECORD => indexedClassLoader.dumpPreloadableClassNames();
			
		} else {
			SprinbootFastClassLoaderRunner.run(args);
		}
	}
	
	
	
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
