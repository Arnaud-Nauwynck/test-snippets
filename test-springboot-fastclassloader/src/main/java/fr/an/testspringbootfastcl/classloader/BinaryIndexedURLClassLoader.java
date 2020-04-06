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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import sun.misc.Resource;
import sun.misc.URLClassPath;

/**
 * A custom ClassLoader that indexes the contents of classpath elements, for
 * faster class locating.
 *
 * The standard URLClassLoader does a linear scan of the classpath for each
 * class or resource, which becomes prohibitively expensive for classpaths with
 * many elements.
 * 
 * Notice: extends URLClassLoader because some libraries do explicit downcast! like liquibase... 
 */
@SuppressWarnings("restriction")
public class BinaryIndexedURLClassLoader extends URLClassLoader { 

    static {
        registerAsParallelCapable(); // TOCHECK ??
    }
    
    private static final String BINARY_FORMAT_VERSION = "v1";
    
    /* The search path for classes and resources */
    private final URLClassPath ucp;

    private List<String> loadedClassNames = new ArrayList<>();
    
    private ClassLoaderPreloadMode preloadMode;

    private File preloadClassNamesFile;
    private File binaryJarPreloadFile;

    private List<String> prefixClassesNotInBinaryJar;
    private List<String> prefixURLsNotInBinaryJar;

    private Object lockBinaryJarPreload = new Object();
    private DataOutputStream binaryJarPreloadOut = null; // in record mode

    private final AtomicInteger statCount_findClass = new AtomicInteger();
    private final AtomicLong statSumNanos_findClass = new AtomicLong();

    private final AtomicInteger statCount_defineClass = new AtomicInteger();
    private final AtomicLong statSumNanos_defineClass = new AtomicLong();
    
    private final AtomicInteger statCount_definePackage = new AtomicInteger();
    private final AtomicLong statSumNanos_definePackage = new AtomicLong();

    private final AtomicInteger statCount_defineClass_preload = new AtomicInteger();
    private final AtomicLong statSumNanos_defineClass_preload = new AtomicLong();

    private final AtomicInteger statCount_definePackage_preload = new AtomicInteger();
    private final AtomicLong statSumNanos_definePackage_preload = new AtomicLong();
    

    /** debug mode: for logging to file all API calls to this ClassLoader */
    private PrintStream auditPrinter; 

    /** debug mode */
    private boolean verbose;

    
    // --------------------------------------------------------------------------------------------
    
    public BinaryIndexedURLClassLoader(ClassLoader parent, 
    		URLClassPath ucp, 
            Collection<String> prefixClassesNotInBinaryJar,
            Collection<String> prefixURLsNotInBinaryJar,
            File binaryClassPreloadFile,
            File preloadClassNamesFile) {
        super(ucp.getURLs(), parent);
        this.ucp = ucp;
        this.prefixClassesNotInBinaryJar = (prefixClassesNotInBinaryJar != null)? new ArrayList<>(prefixClassesNotInBinaryJar) : new ArrayList<>();
        this.prefixURLsNotInBinaryJar = (prefixURLsNotInBinaryJar != null)? new ArrayList<>(prefixURLsNotInBinaryJar) : new ArrayList<>();
        this.binaryJarPreloadFile = (binaryClassPreloadFile != null)? binaryClassPreloadFile : new File("log/preload-BinaryIndexedURLClassLoader.data");
        this.preloadClassNamesFile = (preloadClassNamesFile != null)? preloadClassNamesFile : new File("log/loaded-classNames.txt");
    }

    public void beginAuditTo(File toFile) {
        try {
            this.auditPrinter = new PrintStream(new BufferedOutputStream(new FileOutputStream(toFile)));
        } catch (Exception ex) {
            logError("Failed to open file " + toFile, ex);
        }
    }

    public void endAuditTo() {
        PrintStream tmp = this.auditPrinter;
        if (tmp != null) {
            this.auditPrinter = null;
            try {
                tmp.close();
            } catch(Exception ex) {
                logWarn("Failed to close file .. ignore anyway", ex);
            }
        }
    }


    public void preloaderInit(ClassLoaderPreloadMode mode) {
        this.preloadMode = mode;
        switch(mode) {
        case PRELOAD_BY_NAME_PARALLEL:
            parallelPreloadClassByNames(false);
            break;
        case PRELOAD_BY_NAME_PARALLEL_BLOCK:
            parallelPreloadClassByNames(true);
            break;
        case PRELOAD_BINARY_CLASS_ASYNC:
            loadBinaryPreloadClasses(true);
            break;
        case PRELOAD_BINARY_CLASS_BLOCK:
            loadBinaryPreloadClasses(false);
            break;
        case RECORD_BINARY_CLASS_PRELOAD:
            startRecordBinary();
            break;
        default:
            break;
        }
    }
    
    public void preloaderEnd() {
        boolean dumpStats = false;
        switch(preloadMode) {
        case RECORD_CLASSNAMES:
            dumpPreloadableClassNames();
            break;
        case RECORD_BINARY_CLASS_PRELOAD:
            endRecordBinary();
            break;
        case PRELOAD_BINARY_CLASS_ASYNC:
            dumpStats = true;
            break;
        case PRELOAD_BINARY_CLASS_BLOCK:
            dumpStats = true;
            break;
        default:
            break;
        }

        if (dumpStats) {
            log("preload end => stats: " +
                    "findClass(): " + formatStat(statCount_findClass, statSumNanos_findClass) + 
                    " defineClass(): " + formatStat(statCount_defineClass, statSumNanos_defineClass) + 
                    " defineClass_preload(): " + formatStat(statCount_defineClass_preload, statSumNanos_defineClass_preload) + 
                    ((statSumNanos_definePackage.get() > 1000)? 
                            " definePackage(): " + formatStat(statCount_definePackage, statSumNanos_definePackage) +
                            " definePackage_preload(): " + formatStat(statCount_definePackage_preload, statSumNanos_definePackage_preload)
                        : "")
                    );
        }
        
        this.preloadMode = ClassLoaderPreloadMode.DEFAULT;
    }
    
    private static String formatStat(AtomicInteger statCount, AtomicLong statSumNanos) {
        int count = statCount.get();
        long nanos = statSumNanos.get();
        if (count == 0) {
            return "0";
        }
        long millis = TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS);
        // double avg = ((double)millis) / 
        return millis + "ms (" + count + " times)";
    }
    
    
    protected void audit(String msg) {
        if (auditPrinter != null) {
            auditPrinter.println(msg);
        }
    }
    
    // @Override java.langClassLoader java.net.URLClassLoader
    // --------------------------------------------------------------------------------------------
    
    @Override
    public URL findResource(String name) {
        if (this.verbose) {
            log("IndexedClassLoader finding resource " + name);
        }
        URL res = ucp.findResource(name, false);
        audit("findResource(" + name + ") => " + res);
        return res;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        long nanosStart = System.currentTimeMillis();
        try {
            if (this.verbose) {
                log("IndexedURLClassLoader finding class " + className);
            }
            Object classLoadingLock = getClassLoadingLock(className);
            // synchronized(classLoadingLock) { // useless?
                Class<?> loaded = findLoadedClass(className);
                if (loaded != null) {
                    return loaded;
                }
            // }
            try {
                String path = className.replace('.', '/').concat(".class");
                Resource resource = ucp.getResource(path);
                if (resource != null) {
                    int i = className.lastIndexOf('.');
                    if (i != -1) {
                        String pkgname = className.substring(0, i);
                        // Check if package already loaded.
                        Package pkg = getPackage(pkgname);
                        if (pkg == null) {
                            audit("findClass(" + className + ") => definePackage(" + pkgname + ")");
                            if (preloadMode == ClassLoaderPreloadMode.RECORD_BINARY_CLASS_PRELOAD) {
                                recordBinaryDefinePackage(pkgname);
                            }
                            definePackage(pkgname, null, null, null, null, null, null, null);
                        }
                    }
                    byte[] data = resource.getBytes();
                    // Add a CodeSource via a ProtectionDomain, as code may use this to find its own jars.
                    URL codeSourceURL = resource.getCodeSourceURL();
                    CodeSource cs = new CodeSource(codeSourceURL, (Certificate[]) null);
                    PermissionCollection pc = new Permissions();
                    pc.add(new AllPermission());
                    ProtectionDomain pd = new ProtectionDomain(cs, pc);
                    
                    boolean isPreloadableClass = isPreloadableClass(className);
                    boolean isInBinaryJar = isPreloadableClass && isPrefixClassInBinaryJar(className);
                    if (preloadMode == ClassLoaderPreloadMode.RECORD_BINARY_CLASS_PRELOAD) {
                        if (isPreloadableClass) {
                            recordBinaryBeforeDefineClass(className);
                        }
                    }
                    
                    Class<?> res;
                    synchronized(classLoadingLock) {
                        res = findLoadedClass(className);
                        if (null == res) {
                            long nanosBeforeDefineClass = System.nanoTime();
                            
                            // *** The Biggy ***
                            res = super.defineClass(className, data, 0, data.length, pd);
    
                            long nanosDefineClass = System.nanoTime() - nanosBeforeDefineClass;
                            statSumNanos_defineClass.addAndGet(nanosDefineClass);
                            statCount_defineClass.incrementAndGet();
                            
                            if (preloadMode == ClassLoaderPreloadMode.RECORD_BINARY_CLASS_PRELOAD) {
                                if (isPreloadableClass) {
                                    recordBinaryDefineClass(isInBinaryJar, className, data, pd);
                                }
                            } else if (preloadMode == ClassLoaderPreloadMode.RECORD_CLASSNAMES) {
                                if (isPreloadableClass(className)) {
                                    this.loadedClassNames.add(className);
                                }
                            }
                        }
                    }
                    
                    URL resourceURL = resource.getURL();
                    audit("findClass(" + className + ") => defineClass data.length:" + data.length + " " + 
                            resourceURL + " codeSourceURL:" + codeSourceURL);
                    return res;
                } else {
                    audit("findClass(" + className + ") => ClassNotFoundException");
                    throw new ClassNotFoundException(String.format("IndexedURLClassLoader can't find class %s", className));
                }
            } catch (IOException e) {
                audit("findClass(" + className + ") => ClassNotFoundException");
                throw new ClassNotFoundException(String.format("IndexedURLClassLoader failed to read class %s", className), e);
            }
        } finally {
            long nanos = System.nanoTime() - nanosStart;
            statSumNanos_findClass.addAndGet(nanos);
            statCount_findClass.incrementAndGet();            
        }
    }

    @Override
    public Enumeration<URL> findResources(final String name) throws IOException {
        final Enumeration<URL> tmpres = ucp.findResources(name, true);
        List<URL> ls = EnumerationUtils.toList(tmpres);
        audit("findResources(" + name + ") => " + ls.size() + " elt(s) : " + ls);
        return EnumerationUtils.toEnumeration(ls);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> res = super.loadClass(name);
        audit("loadClass(" + name + ") => ..");
        return res;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> res = super.loadClass(name, resolve);
        audit("loadClass(" + name + ", " + resolve + ") => ..");
        return res;
    }

    @Override
    public URL getResource(String name) {
        URL res = super.getResource(name);
        audit("getResource(" + name + ") => '" + res + "'");
        return res;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> tmpres = super.getResources(name);
        List<URL> ls = EnumerationUtils.toList(tmpres);
        audit("getResources(" + name + ") => " + ls.size() + " elt(s) : " + ls);
        return EnumerationUtils.toEnumeration(ls);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream res = super.getResourceAsStream(name);
        audit("getResourceAsStream(" + name + ") => " + ((res != null)? ".." : "null"));
        return res;
    }

    @Override
    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor,
            String implTitle, String implVersion, String implVendor, URL sealBase
            ) throws IllegalArgumentException {
        long nanosBefore = System.nanoTime();

        Package res = super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
        audit("definePackage(" + name + ", .." + 
                ((sealBase != null)? " sealBase:" + sealBase : "") + ") => ..");
        
        long nanos = System.nanoTime() - nanosBefore;
        statSumNanos_definePackage.addAndGet(nanos);
        statCount_definePackage.incrementAndGet();
        
        return res;
    }

    @Override
    protected Package getPackage(String name) {
        Package res = super.getPackage(name);
        audit("getPackage(" + name + ") => ..");
        return res;
    }

    @Override
    protected Package[] getPackages() {
        Package[] res = super.getPackages();
        audit("getPackages() => " + res.length + " elt(s) : " + Arrays.asList(res));
        return res;
    }
    
    // --------------------------------------------------------------------------------------------

    
    public List<String> getLoadedClassNames() {
        return new ArrayList<>(loadedClassNames);
    }

    public List<String> getPreloadableClassNames() {
        List<String> res = new ArrayList<>();
        for(String e : getLoadedClassNames()) {
            if (isPreloadableClass(e)) {
                res.add(e);
            }
        }
        return res;
    }

    private boolean isPreloadableClass(String className) {
        return !className.startsWith("java.") && ! className.startsWith("com.sun.") && 
                !className.contains("$Proxy") && !className.contains("$$") && 
                !className.contains("CGLIB$");
    }

    private boolean isPreloadableJarClass(String className) {
        return isPreloadableClass(className) && isPrefixClassInBinaryJar(className);
    }

    private boolean isPrefixClassInBinaryJar(String className) {
        return null == findFirstPrefix(className, prefixClassesNotInBinaryJar);
    }

    public List<String> getPreloadableJarClassNames() {
        List<String> res = new ArrayList<>();
        for(String className : getLoadedClassNames()) {
            if (isPreloadableJarClass(className)) {
                res.add(className);
            }
        }
        return res;
    }
    
    protected static String findFirstPrefix(String text, Collection<String> prefixes) {
        for(String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                return prefix;
            }
        }
        return null;
    }
    
    public void dumpPreloadableClassNames() {
        log("dump preload classNames file: " + preloadClassNamesFile.getAbsoluteFile());
        List<String> classNames = getPreloadableClassNames();
        Writer out = null;
        try {
            out = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(preloadClassNamesFile)));
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
            in = new BufferedReader(new InputStreamReader(new FileInputStream(preloadClassNamesFile)));
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
        log("parallel preloading " + classNames.size() + " class by name...");
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(3); // TOCHECK
        try {
            parallelPreloadClassByNames(classNames, executorService);
        } finally {
            executorService.shutdown();
        }
        long millis = System.currentTimeMillis() - startTime;
        log("... done parallel preloading " + classNames.size() + " class by name: took " + millis + " ms");
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
            logWarn("Failed to preload class by name: " + className + ", ignore, no rethrow!", ex);
            // ignore, no rethrow!
        }
    }


    private void startRecordBinary() {
        try {
            binaryJarPreloadOut = new DataOutputStream(
                    new DeflaterOutputStream(
                            new BufferedOutputStream(new FileOutputStream(binaryJarPreloadFile))));
        
            binaryJarPreloadOut.writeUTF(BINARY_FORMAT_VERSION);
            
            // header classpath
            URL[] urls = getURLs();
            binaryJarPreloadOut.writeInt(urls.length);
            for(URL url : urls) {
                binaryJarPreloadOut.writeUTF(url.toString());
            }
            
            // header prefixClassesNotInBinaryJar
            binaryJarPreloadOut.writeInt(prefixClassesNotInBinaryJar.size());
            for(String s : prefixClassesNotInBinaryJar) {
                binaryJarPreloadOut.writeUTF(s);
            }
            
            // header prefixURLsNotInBinaryJar
            binaryJarPreloadOut.writeInt(prefixURLsNotInBinaryJar.size());
            for(String s : prefixURLsNotInBinaryJar) {
                binaryJarPreloadOut.writeUTF(s);
            }
            
        } catch(Exception ex) {
            logError("Failed to open file " + binaryJarPreloadFile + " in write mode", ex);
        }
    }

    private void endRecordBinary() {
        this.preloadMode = ClassLoaderPreloadMode.DEFAULT;
        synchronized (lockBinaryJarPreload) {
            DataOutputStream out = binaryJarPreloadOut;
            binaryJarPreloadOut = null;
            try {
                out.writeByte('E');
                out.close();
            } catch (IOException ex) {
                throw new RuntimeException("Failed", ex);
            }
        }
    }
    
    private void loadBinaryPreloadClasses(boolean async) {
        if (!binaryJarPreloadFile.exists()) {
            log("File no found " + binaryJarPreloadFile + " .. ignore loadBinaryPreloadClasses");
        }
        log("preloading classes using binary data " + binaryJarPreloadFile);
        long startTime = System.currentTimeMillis();
        
        boolean continueRead = true;

        ExecutorService classLoaderExecutor = Executors.newFixedThreadPool(1);
        List<Future<?>> futures = new ArrayList<>(15000);
        DataInputStream in = null;
        try {
            in = new DataInputStream(
                    new InflaterInputStream(
                            new BufferedInputStream(new FileInputStream(binaryJarPreloadFile))));

            // check header: binary format version
            String binaryFormatVersion = in.readUTF();
            if (!binaryFormatVersion.equals(BINARY_FORMAT_VERSION)) {
                log("binary format version differs: " + binaryFormatVersion + ", expecting " + BINARY_FORMAT_VERSION); 
                continueRead = false;
            }
            
            // check header: classpath
            if (continueRead) {
                URL[] urls = getURLs();
                int urlCount = in.readInt();
                if (urls.length != urlCount) {
                    log("classpath urls.lengh differs: " + urlCount + ", expecting " + urls.length);
                    continueRead = false;
                } else {
                    for(int i = 0; i < urlCount; i++) {
                        String url = in.readUTF();
                        if (!url.equals(urls[i].toString())) {
                            log("classpath url[" + i + "] differs: " + url + ", expecting " + urls[i].toString());
                            continueRead = false;
                            break;
                        }
                    }
                }
            }
            
            // header prefixClassesNotInBinaryJar
            if (continueRead) {
                int count = in.readInt();
                if (count != prefixClassesNotInBinaryJar.size()) {
                    log("prefixClassesNotInBinaryJar lengh differs: " + count + ", expecting " + prefixClassesNotInBinaryJar.size());
                    continueRead = false;
                } else {
                    for(int i = 0; i < count; i++) {
                        String prefix = in.readUTF();
                        if (!prefix.equals(prefixClassesNotInBinaryJar.get(i).toString())) {
                            log("prefixClassesNotInBinaryJar[" + i + "] differs: " + prefix + ", expecting " + prefixClassesNotInBinaryJar.get(i));
                            continueRead = false;
                            break;
                        }
                    }
                }
            }
            
            // header prefixURLsNotInBinaryJar
            if (continueRead) {
                int count = in.readInt();
                if (count != prefixURLsNotInBinaryJar.size()) {
                    log("prefixURLsNotInBinaryJar lengh differs: " + count + ", expecting " + prefixURLsNotInBinaryJar.size());
                    continueRead = false;
                } else {
                    for(int i = 0; i < count; i++) {
                        String prefix = in.readUTF();
                        if (!prefix.equals(prefixURLsNotInBinaryJar.get(i).toString())) {
                            log("prefixURLsNotInBinaryJar[" + i + "] differs: " + prefix + ", expecting " + prefixURLsNotInBinaryJar.get(i));
                            continueRead = false;
                            break;
                        }
                    }
                }
            }

            if (continueRead) {
                for(;;) {
                    byte type = in.readByte();
                    if (type == 'E') {
                        break;
                    }
                    switch(type) {
                    case 'c': 
                        reloadBinaryBeforeDefineClass(in, classLoaderExecutor);
                        break;
                    case 'C':
                        futures.add(reloadBinaryDefineClass(in, classLoaderExecutor));
                        break;
                    case 'L':
                        futures.add(reloadBinaryLoadClassByName(in, classLoaderExecutor));
                        break;
                    case 'P':
                        futures.add(reloadBinaryDefinePackage(in, classLoaderExecutor));
                        break;
                    }
                }
            }
        } catch(IOException ex) {
            // throw new RuntimeException("Failed", ex);
            logWarn("Failed to read preload file: " + binaryJarPreloadFile + " => abort preload", ex);
            continueRead = false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch(Exception ex) {
                }
            }
        }
        
        if (!continueRead) {
            log(" => ignore preload + delete file, switch to preloadMode=DEFAULT");
            this.preloadMode = ClassLoaderPreloadMode.DEFAULT;
            try {
                binaryJarPreloadFile.delete();
            } catch(Exception ex) {
                logWarn("Failed to delete file " + binaryJarPreloadFile + " .. ignore", ex);
            }
        }
        
        long readMillis = System.currentTimeMillis() - startTime;
        log(".. done io reading classes data, took: " + readMillis + " ms");

        if (async) {
            new Thread(() -> waitFinishPreloading(startTime, classLoaderExecutor, futures)).start();
        } else {
            waitFinishPreloading(startTime, classLoaderExecutor, futures);
        }
    }

    private void waitFinishPreloading(long startTime, ExecutorService classLoaderExecutor, List<Future<?>> futures) {
        for(Future<?> f : futures) {
    		try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException("Failed f.get()", e);
			}
        }
        classLoaderExecutor.shutdown();
        
        long millis = System.currentTimeMillis() - startTime;
        log(".. done preloading classes using binary data, took: " + millis + " ms");
    }
    
    private void recordBinaryBeforeDefineClass(String className) {
        synchronized (lockBinaryJarPreload) {
            DataOutputStream out = binaryJarPreloadOut;
            try {
                out.writeByte('c');
                out.writeUTF(className);
            } catch (IOException ex) {
                throw new RuntimeException("Failed", ex);
            }
        }
    }
            
    private void recordBinaryDefineClass(boolean inBinaryJar, String className, byte[] data, ProtectionDomain pd) {
        synchronized (lockBinaryJarPreload) {
            DataOutputStream out = binaryJarPreloadOut;
            try {
                if (inBinaryJar) {
                    out.writeByte('C');
                    out.writeUTF(className);
                    out.writeInt(data.length);
                    out.write(data);
                    // TODO out.writeUTF(pd);
                    URL codeSourceURL = pd.getCodeSource().getLocation();
                    out.writeBoolean(codeSourceURL != null);
                    if (codeSourceURL != null) {
                        out.writeUTF(codeSourceURL.toString());
                    }
                } else {
                    out.writeByte('L');
                    out.writeUTF(className);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Failed", ex);
            }
        }
    }
    
    private void reloadBinaryBeforeDefineClass(DataInputStream in, ExecutorService classLoaderExecutor) throws IOException {
        String className = in.readUTF();
        // do nothing yet
    }
    
    private Future<?> reloadBinaryDefineClass(DataInputStream in, ExecutorService classLoaderExecutor) throws IOException {
        String className = in.readUTF();
        int dataLen = in.readInt();
        byte[] data = new byte[dataLen];
        in.readFully(data);
        URL codeSourceURL = (in.readBoolean())? safeURL(in.readUTF()) : null;

        return classLoaderExecutor.submit(() -> {
            try {
                synchronized(getClassLoadingLock(className)) {
                    if (null == super.findLoadedClass(className)) {
                        CodeSource cs = new CodeSource(codeSourceURL, (Certificate[]) null);
                        PermissionCollection pc = new Permissions();
                        pc.add(new AllPermission());
                        ProtectionDomain pd = new ProtectionDomain(cs, pc);

                        long nanosBeforeDefineClass = System.nanoTime();
                        
                        // *** The Biggy ***
                        super.defineClass(className, data, 0, data.length, pd);
                        
                        long nanosDefineClass = System.nanoTime() - nanosBeforeDefineClass;
                        statSumNanos_defineClass_preload.addAndGet(nanosDefineClass);
                        statCount_defineClass_preload.incrementAndGet();
                    }
                }
            } catch(Exception ex) {
                // log("Failed reloadBinary => defineClass " + className + " ..ignore, no rethrow!");
                // ignore, no rethrow!
            }
        });
    }
    
    private Future<?> reloadBinaryLoadClassByName(DataInputStream in, ExecutorService classLoaderExecutor) throws IOException {
        String className = in.readUTF();
        return classLoaderExecutor.submit(() -> {
            try {
                loadClass(className);
            } catch(Exception ex) {
                // log("Failed reloadBinary => loadClass " + className + " .. ignore " + ex.getMessage());
                // ignore, no rethrow!
            }
        });
    }

    private void recordBinaryDefinePackage(String packageName) {
        synchronized (lockBinaryJarPreload) {
            DataOutputStream out = binaryJarPreloadOut;
            try {
                out.writeByte('P');
                out.writeUTF(packageName);
            } catch (IOException ex) {
                throw new RuntimeException("Failed", ex);
            }
        }
    }
    
    private Future<?> reloadBinaryDefinePackage(DataInputStream in, ExecutorService classLoaderExecutor) throws IOException {
        String packageName = in.readUTF();
        return classLoaderExecutor.submit(() -> {
            // TOCHECK .. synchronized(getClassLoadingLock(className))
            Package pkg = getPackage(packageName);
            if (pkg == null) {
                try {
                    long nanosBefore = System.nanoTime();
                    
                    super.definePackage(packageName, null, null, null, null, null, null, null);

                    long nanos = System.nanoTime() - nanosBefore;
                    statSumNanos_definePackage_preload.addAndGet(nanos);
                    statCount_definePackage_preload.incrementAndGet();

                } catch(Exception ex) {
                    // log("Failed reloadBinary definePackage " + packageName + " .. ignore " + ex.getMessage());
                    // ignore, no rethrow!
                }
            }
        });
    }
    
    public void dumpBinaryJarPreloadFile(PrintStream out) {
        if (!binaryJarPreloadFile.exists()) {
            log("File no found " + binaryJarPreloadFile + " .. ignore dump");
            return;
        }
        log("dump binary data " + binaryJarPreloadFile);
        
        DataInputStream in = null;
        try {
            in = new DataInputStream(
                    new InflaterInputStream(
                            new BufferedInputStream(new FileInputStream(binaryJarPreloadFile))));
            for(;;) {
                byte type = in.readByte();
                if (type == 'E') {
                    break;
                }
                switch(type) {
                case 'c': {
                    String className = in.readUTF();
                    out.println("c " + className);
                } break;
                case 'C': {
                    String className = in.readUTF();
                    int dataLen = in.readInt();
                    byte[] data = new byte[dataLen];
                    in.readFully(data);
                    URL codeSourceURL = (in.readBoolean())? safeURL(in.readUTF()) : null;
                    out.println("C " + className + " " + dataLen + " .. " + codeSourceURL);
                } break;
                case 'L': {
                    String className = in.readUTF();
                    out.println("L " + className);
                } break;
                case 'P': {
                    String packageName = in.readUTF();
                    out.println("P " + packageName);
                } break;
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
    }

    private static URL safeURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            log("should not occurs: " + e.getMessage());
            return null;
        }
    }

    private static void log(String msg) {
        // since this code is used at bottstrap time... better to use System.out instead of slf4j Logger
        System.out.println(msg); 
    }

    private static void logWarn(String msg, Throwable ex) {
        System.err.println(msg + ((ex != null)? ", ex:" + ex.getMessage() : ""));
    }

    private static void logError(String msg, Throwable ex) {
        System.err.println(msg);
        if (ex != null) {
            ex.printStackTrace(System.err);
        }
    }
    
}
