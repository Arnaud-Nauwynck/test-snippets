package fr.an.testspringbootfastcl.classloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

/**
 * A custom ClassLoader that indexes the contents of classpath elements, for
 * faster class locating.
 *
 * The standard URLClassLoader does a linear scan of the classpath for each
 * class or resource, which becomes prohibitively expensive for classpaths with
 * many elements.
 */
public class IndexedClassLoader extends ClassLoader {

	// Whether we print out verbose information about each class we load.
	private boolean verbose;

	/* The search path for classes and resources */
	private final ScanResult scanResult;
	
	private Map<String,URL[]> resourceToURLs;

	private List<String> loadedClassNames = new ArrayList<>();
	
	public static enum PreloadMode {
		DEFAULT,
		RECORD_CLASSNAMES,
		RECORD_BINARY_CLASS_PRELOAD,
		PRELOAD_BY_NAME_PARALLEL,
		PRELOAD_BY_NAME_PARALLEL_BLOCK,
		PRELOAD_BINARY_CLASS
	}
	
	private PreloadMode preloadMode;

	private File classNamesFile = new File("loaded-classNames.txt");
	private File binaryClassPreloadFile = new File("preload-classes.data");

	
	// --------------------------------------------------------------------------------------------
	
	public IndexedClassLoader(ClassLoader parent, ScanResult scanResult) {
		super(parent);
		this.scanResult = scanResult;
		this.verbose = false;
		this.resourceToURLs = scanResultToResourceUrls(scanResult);
		
		if (verbose) {
			System.out.println("IndexedClassLoader => found " + resourceToURLs.size() + " resource(s)");
		}
	}

	public void preloaderInit(PreloadMode mode) {
		this.preloadMode = mode;
		switch(mode) {
		case PRELOAD_BY_NAME_PARALLEL:
			parallelPreloadClassByNames(false);
			break;
		case PRELOAD_BY_NAME_PARALLEL_BLOCK:
			parallelPreloadClassByNames(true);
			break;
		case PRELOAD_BINARY_CLASS:
			loadBinaryPreloadClasses();
			break;
		default:
			break;
		}
	}
	
	public void preloaderEnd() {
		switch(preloadMode) {
		case RECORD_CLASSNAMES:
			dumpPreloadableClassNames();
			break;
		case RECORD_BINARY_CLASS_PRELOAD:
			dumpBinaryPreloadClasses();
			break;
		default:
			break;
		}
	}
	
	// extends ClassLoader
	// --------------------------------------------------------------------------------------------

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (this.verbose) {
			System.out.println(String.format("[IndexedURLClassLoader finding class %s]", className));
		}
		try {
			String resourceName = className.replace('.', '/').concat(".class");
			
			try (ResourceList resources = scanResult.getResourcesWithPath(resourceName)) {
				if (! resources.isEmpty()) {
					Resource resource = resources.get(0);

					URL classpathEltURL = resource.getClasspathElementURL(); 
					URL codeSourceURL = classpathEltURL; // TOCHECK embedded jar ...  .jar!/BOOT-INF/ ..

					definePackageIfNeeded(className);
					
					byte[] data = resource.load();
					try {
						CodeSource cs = new CodeSource(codeSourceURL, (Certificate[]) null);
						PermissionCollection pc = new Permissions();
						pc.add(new AllPermission());
						ProtectionDomain pd = new ProtectionDomain(cs, pc);
						
						Class<?> res = defineClass(className, data, 0, data.length, pd);
						
						this.loadedClassNames.add(className);
						
						return res;
					} finally {
						resource.close();
					}
				} else {
					throw new ClassNotFoundException(String.format("IndexedURLClassLoader can't find class %s", className));
				}
			}
		} catch (Exception e) {
			throw new ClassNotFoundException(String.format("IndexedURLClassLoader failed to read class %s", className), e);
		}
	}

	// need to definePackage for some libraries..
	// example: org.yaml.snakeyaml.introspector.PropertyUtils
	private void definePackageIfNeeded(String className) {
		int idxPackageEnd = className.lastIndexOf('.');
		if (idxPackageEnd != -1) {
			String pkgname = className.substring(0, idxPackageEnd);
			// Check if package already loaded.
			Package pkg = getPackage(pkgname);
			if (pkg == null) {
				definePackage(pkgname, null, null, null, null, null, null, null);
			}
		}
	}


	@Override
	public URL findResource(String resourceName) {
		if (this.verbose) {
			System.out.println(String.format("[IndexedClassLoader finding resource %s]", resourceName));
		}
		URL[] urls = resourceToURLs.get(resourceName);
		if (urls != null) {
			return urls[0];
		}
		if (resourceName.equals("/")) {
			return null;
		}
		if (resourceName.endsWith("/")) {
			String resourceNameWithoutSlash = resourceName.substring(0, resourceName.length() - 1);
			urls = resourceToURLs.get(resourceNameWithoutSlash);
			if (urls != null) {
				return urlWithSlash(urls[0]);
			}
		}
		return null;
	}
	
	@Override
	public Enumeration<URL> findResources(final String resourceName) throws IOException {
		List<URL> urls = null;
		
		if (resourceName == null || resourceName.equals("/")) {
			urls = Collections.emptyList();
		}
		if (urls == null) {
			URL[] resourceUrls = resourceToURLs.get(resourceName);
			if (resourceUrls != null) {
				urls = Arrays.asList(resourceUrls);
			}
		}
		if (urls == null && resourceName.endsWith("/")) {
			URL[] resourceUrlsWithoutSlash = resourceToURLs.get(resourceName.substring(0, resourceName.length()-1));
			if (resourceUrlsWithoutSlash != null) {
				urls = new ArrayList<>();
				for(URL resourceUrlWithoutSlash : resourceUrlsWithoutSlash) {
					urls.add(urlWithSlash(resourceUrlWithoutSlash));
				}
			}
		}
		
		if (urls == null) {
			urls = Collections.emptyList();
		}
		Iterator<URL> iter = urls.iterator();
		return new Enumeration<URL>() {
			public URL nextElement() {
				return iter.next();
			}
			public boolean hasMoreElements() {
				return iter.hasNext();
			}
		};
		
	}

	
	// --------------------------------------------------------------------------------------------
	
	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean p) {
		this.verbose = p;
	}
	
	public List<String> getLoadedClassNames() {
		return new ArrayList<>(loadedClassNames);
	}

	public List<String> getPreloadableClassNames() {
		List<String> res = new ArrayList<>();
		for(String e : getLoadedClassNames()) {
			if (!e.startsWith("java.") && ! e.startsWith("com.sun.") && 
					!e.contains("$Proxy") && !e.contains("$$") && !e.contains("CGLIB$")
					) {
				res.add(e);
			}
		}
		return res;
	}

	public List<String> getPreloadableJarClassNames() {
		List<String> res = new ArrayList<>();
		for(String e : getPreloadableClassNames()) {
			if (!e.startsWith("fr.an.")) { // TODO hack.. should test classes from local dir, not jar
				res.add(e);
			}
		}
		return res;
	}
	
	public void dumpPreloadableClassNames() {
		System.out.println("dump preload classNames file: " + classNamesFile.getAbsoluteFile());
		List<String> classNames = getPreloadableClassNames();
		Writer out = null;
		try {
			out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(classNamesFile)));
			for(String className : classNames) {
				out.write(className);
				out.write('\n');
			}
		} catch(IOException ex) {
			throw new RuntimeException("Failed", ex);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch(Exception ex) {
				}
			}
		}
	}
	
	public void parallelPreloadClassByNames(boolean wait) {
		List<String> classNames = new ArrayList<>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(classNamesFile)));
			String line;
			while(null != (line = in.readLine())) {
				if (line.startsWith("#") || line.trim().isEmpty()) {
					// ignore comment
				}
				classNames.add(line);
			}
		} catch(IOException ex) {
			throw new RuntimeException("Failed", ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch(Exception ex) {
				}
			}
		}


		if (wait) {
			parallelPreloadClassByNames(classNames);
		} else {
			new Thread(() -> {
				parallelPreloadClassByNames(classNames);
			}).start();
		}
	}

	private void parallelPreloadClassByNames(List<String> classNames) {
		System.out.println("parallel preloading " + classNames.size() + " class by name...");
		long startTime = System.currentTimeMillis();
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		try {
			parallelPreloadClassByNames(classNames, executorService);
		} finally {
			executorService.shutdown();
		}
		long millis = System.currentTimeMillis() - startTime;
		System.out.println("... done parallel preloading " + classNames.size() + " class by name: took " + millis + " ms");
	}
	
	public void parallelPreloadClassByNames(Collection<String> classNames, ExecutorService executorService) {
		List<Future<?>> futures = new ArrayList<>();
		// submit all
		for(String className : classNames) {
			futures.add(executorService.submit(() -> safePreloadClassByName(className)));			
		}
		// wait all
		for(Future<?> f : futures) {
			try {
				f.get();
			} catch (InterruptedException e) {
				break;
			} catch (ExecutionException e) {
				// ignore
			}
		}
	}
	
	private void safePreloadClassByName(String className) {
		try {
			loadClass(className);
		} catch(Exception ex) {
			System.out.println("Failed to preload class by name: " + className + ", ignore, no rethrow! ex:" + ex.getMessage());
			// ignore, no rethrow!
		}
	}

	private void dumpBinaryPreloadClasses() {
		System.out.println("dump preload classNames file: " + binaryClassPreloadFile.getAbsoluteFile());
		List<String> classNames = getPreloadableJarClassNames();
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(binaryClassPreloadFile)));
			for(String className : classNames) {
				
				String resourceName = className.replace(".", "/") + ".class";
				try (ResourceList resources = scanResult.getResourcesWithPath(resourceName)) {
					if (! resources.isEmpty()) {
						Resource resource = resources.get(0);
						URL classpathEltURL = resource.getClasspathElementURL();
						byte[] data = resource.load();
						
						out.writeUTF(className);
						out.writeUTF(classpathEltURL.toString());
						out.writeInt(data.length);
						out.write(data);
						
						resource.close();
					}
				}
			}
			
			out.writeUTF("-- end classes");
		} catch(IOException ex) {
			throw new RuntimeException("Failed", ex);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch(Exception ex) {
				}
			}
		}
	}

	private void loadBinaryPreloadClasses() {
		if (!binaryClassPreloadFile.exists()) {
			System.out.println("Fileno found " + binaryClassPreloadFile + " .. ignore loadBinaryPreloadClasses");
		}
		long startTime = System.currentTimeMillis();
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(binaryClassPreloadFile)));
			for(;;) {
				String className = in.readUTF();
				if (className.equals("-- end classes")) {
					break;
				}
				URL codeSourceURL = safeURL(in.readUTF());
				int dataLen = in.readInt();
				byte[] data = new byte[dataLen];
				in.read(data);
				
				try {
					CodeSource cs = new CodeSource(codeSourceURL, (Certificate[]) null);
					PermissionCollection pc = new Permissions();
					pc.add(new AllPermission());
					ProtectionDomain pd = new ProtectionDomain(cs, pc);
					
					definePackageIfNeeded(className);
					
					super.defineClass(className, data, 0, data.length, pd);
					
					this.loadedClassNames.add(className);
					
				} catch(Exception ex) {
					System.out.println("Failed to defineClass " + className + " .. ignore, no rethrow ex:" + ex.getMessage());
				}
			}
		} catch(IOException ex) {
			throw new RuntimeException("Failed", ex);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch(Exception ex) {
				}
			}
		}
		
		long millis = System.currentTimeMillis() - startTime;
		System.out.println(".. done preload binary classes, took: " + millis + " ms");
	}
	
	// internal
	// --------------------------------------------------------------------------------------------

	private static Map<String,URL[]> scanResultToResourceUrls(ScanResult scanResult) {
		Map<String,URL[]> res = new HashMap<>();
		for(Resource resource : scanResult.getAllResources()) {
			String path = resource.getPath();
			URL resourceUrl = resource.getURL();
			addResourceUrl(res, path, resourceUrl);
		}
		
		// need also to index dirs!! cf springboot->jpa->hibernate startup finding resource "" 
//		Caused by: java.io.FileNotFoundException: class path resource [] cannot be resolved to URL because it does not exist
//		at org.springframework.core.io.ClassPathResource.getURL(ClassPathResource.java:195)
		List<URL> classPathURLs = scanResult.getClasspathURLs();
		for(URL classPathURL : classPathURLs) {
			// System.out.println(classPathURL);
			if (classPathURL.getProtocol().equals("file")) {
				File file = new File(classPathURL.getFile());
				if (file.exists() && file.isDirectory()) {
					String dirURL = classPathURL.toString();
					if (dirURL.endsWith("/")) {
						dirURL = dirURL.substring(0, dirURL.length() - 1);
					}
					addResourceUrl(res, "", classPathURL); // special case : for "", url end with "/" .. but not for sub-dir?
					recursiveAddDirUrl(res, file, "", dirURL);
				}
			}
		}
		return res;
	}
	
	private static void recursiveAddDirUrl(Map<String,URL[]> res, File currDir, String currResourceName, String currUrl) {
		File[] childDirs = currDir.listFiles(f -> f.isDirectory());
		if (childDirs != null && childDirs.length != 0) {
			for(File childDir : childDirs) {
				String childDirUrl = currUrl + "/" + childDir.getName();
				String childResourceName = ((currResourceName.isEmpty())? "" : currResourceName + "/") + childDir.getName();
				recursiveAddDirUrl(res, childDir, childResourceName, childDirUrl);
				addResourceUrl(res, childResourceName, safeURL(childDirUrl));
			}
		}
	}
	
	private static void addResourceUrl(Map<String,URL[]> res, String path, URL resourceUrl) {
		URL[] resourceUrls = res.get(path);
		if (resourceUrls == null) {
			resourceUrls = new URL[] { resourceUrl };
		} else {
			URL[] prevUrls = resourceUrls;
			resourceUrls = new URL[prevUrls.length + 1];
			System.arraycopy(prevUrls, 0, resourceUrls, 0, prevUrls.length);
			resourceUrls[prevUrls.length] = resourceUrl;
		}
		res.put(path, resourceUrls);
	}

	public static URL[] systemClassPathURLs() {
		try {
			String[] paths = System.getProperties().getProperty("java.class.path").split(File.pathSeparator);
			URL[] urls = new URL[paths.length];
			for (int i = 0; i < paths.length; ++i) {
				urls[i] = new File(paths[i]).toURI().toURL();
			}
			return urls;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}


	private static URL urlWithSlash(URL url) {
		String urlString = url.toString();
		if (urlString.endsWith("/")) {
			return url;
		} else {
			return safeURL(urlString + "/");
		}
	}

	private static URL safeURL(String urlString) {
		try {
			return new URL(urlString);
		} catch (MalformedURLException e) {
			System.out.println("should not occurs: " + e.getMessage());
			return null;
		}
	}
	
}