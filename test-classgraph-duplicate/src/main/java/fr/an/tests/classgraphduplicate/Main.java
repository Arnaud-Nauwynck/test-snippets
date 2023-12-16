package fr.an.tests.classgraphduplicate;

import io.github.classgraph.*;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Main {

    private static final boolean verbose = false;

    record FilePair(File file1, File file2) {}

    @RequiredArgsConstructor
    public static class JarDuplicateStats {
        public final File resolvedJarFile;
        public final File jarWithDuplicateFile;

        // int duplicateCount;
        private final Set<String> duplicateResources = new HashSet<>();
        public void addDuplicateResource(String resource) {
            duplicateResources.add(resource);
        }
    }

    public static void main(String[] args) {
        ClassLoader thisClassLoader = Main.class.getClassLoader();

        ClassGraph classGraph = new ClassGraph();

        classGraph.addClassLoader(thisClassLoader);
        String hadoopHome = System.getenv("HADOOP_HOME");
        if (hadoopHome != null) {
            val hadoopDir = new File(hadoopHome);
            if (hadoopDir.exists()) {
                System.out.println("adding to scan path /jars/*.jar for HADOOP_HOME=" + hadoopHome);
                File jarsDir = new File(hadoopDir, "jars");
                List<URL> jarUrls = new ArrayList<>();
                val jarFiles = jarsDir.listFiles();
                if (jarFiles != null) {
                    for (val jarFile : jarFiles) {
                        if (jarFile.getName().endsWith(".jar")) {
                            try {
                                val jarUrl = jarFile.toURI().toURL();
                                jarUrls.add(jarUrl);
                            } catch (MalformedURLException e) {
                                System.out.println("ignore url " + e.getMessage());
                            }
                        }
                    }
                }
                URLClassLoader hadoopClassLoader = new URLClassLoader(jarUrls.toArray(new URL[0]), thisClassLoader);
                URL checkLoadedResource = hadoopClassLoader.findResource("org/apache/hadoop/fs/FileSystem.class");
                if (checkLoadedResource == null) {
                    System.out.println("Failed to load hadoop resource");
                }

                classGraph.addClassLoader(hadoopClassLoader);
                try {
                    // Class<?> checkLoadedClass = hadoopClassLoader.loadClass("org.apache.spark.sql.Dataset");
                    Class<?> checkLoadedClass = hadoopClassLoader.loadClass("org.apache.hadoop.fs.FileSystem");
                    System.out.println("ok, checked loaded hadoop class " + checkLoadedClass);
                } catch (ClassNotFoundException e) {
                    System.out.println("Failed to loaded hadoop class");
                }
            }
        }
        classGraph.enableClassInfo();

        // Scan the classpath for classes
        try (ScanResult scanResult = classGraph.scan()) {
            printDuplicateResourcesReport(scanResult);
        }
    }

    public static void printDuplicateResourcesReport(ScanResult scanResult) {
        ResourceList allResources = scanResult.getAllResources();
        ClassInfoList allClasses = scanResult.getAllClasses(); // deduplicated classes

        System.out.println("scanned classpath " + allResources.size() + " resources, including " + allClasses.size() + " classes");

        Map<File,Map<String,Resource>> resourceMapByClasspathElementFileMap = new HashMap<>();
        for (val resource: allResources) {
            val file = resource.getClasspathElementFile();
            if (file == null) {
                continue; // should not occur
            }
            val resourceMap = resourceMapByClasspathElementFileMap.computeIfAbsent(file, f -> new HashMap<>());
            resourceMap.put(resource.getPath(), resource);
        }

        List<Entry<String, ResourceList>> duplicatePaths = allResources
                .findDuplicatePaths();
        System.out.println("found " + duplicatePaths.size() + " resource duplicate(s)");
        System.out.println();

        Map<FilePair, JarDuplicateStats> jarDuplicateStatsMap = new HashMap<>();
        int packageInfoCount = 0;
        int moduleInfoCount = 0;
        int noticeCount = 0;
        int licenseCount = 0;
        int metaInfManifestCount = 0;
        int metaInfIndexList = 0;
        int metaInfJandexIdxCount = 0;
        int metaInfOtherCount = 0;
        int innerClassCount = 0;
        int unusedStubClassCount = 0;
        int manifestVmCount = 0;
        int schemaValidationSchemaJsonCount = 0;
        int schemaKubeSchemaJsonCount = 0;

        for(val duplicatePath: duplicatePaths) {
            String duplicatePathKey = duplicatePath.getKey();
            ResourceList duplicatePathResourceList = duplicatePath.getValue();
            int duplicateResourceCount = duplicatePathResourceList.size();
            Resource resolvedResource = duplicatePathResourceList.iterator().next();
            File resolvedClasspathElementFile = resolvedResource.getClasspathElementFile();

            if (duplicatePathKey.endsWith("package-info.class")) {
                packageInfoCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.endsWith("module-info.class")) {
                moduleInfoCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.equals("NOTICE")) {
                noticeCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.equals("LICENSE")) {
                licenseCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.equals("META-INF/MANIFEST.MF")) {
                metaInfManifestCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.equals("META-INF/INDEX.LIST")) {
                metaInfIndexList += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.equals("META-INF/jandex.idx")) {
                metaInfJandexIdxCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.startsWith("META-INF/")) {
                metaInfOtherCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.startsWith("manifest.vm")) {
                manifestVmCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.startsWith("schema/validation-schema.json")) {
                schemaValidationSchemaJsonCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.startsWith("schema/kube-schema.json")) {
                schemaKubeSchemaJsonCount += duplicateResourceCount;
                continue;
            }

            if (duplicatePathKey.contains("$")) {
                // ignore inner classes, as duplicate will be found for corresponding parent class anyway
                innerClassCount += duplicateResourceCount;
                continue;
            }
            if (duplicatePathKey.endsWith("/UnusedStubClass.class")) {
                // ignore special spark class "org.apache.spark.unused.UnusedStubClass"
                unusedStubClassCount += duplicateResourceCount;
                continue;
            }

            ResourceList resources = duplicatePath.getValue();
            if (verbose) System.out.println("Class " + duplicatePathKey + " -> " + resources.size() + " resources, resolved " + resolvedClasspathElementFile);
            for(val resource: resources) {
                File duplicateClassJarFile = resource.getClasspathElementFile();
                if (duplicateClassJarFile.equals(resolvedClasspathElementFile)) {
                    continue;
                }

                JarDuplicateStats jarDuplicateStats = jarDuplicateStatsMap.computeIfAbsent(new FilePair(resolvedClasspathElementFile, duplicateClassJarFile),
                        k -> new JarDuplicateStats(resolvedClasspathElementFile, duplicateClassJarFile));
                jarDuplicateStats.addDuplicateResource(resource.getPath());

                if (verbose) System.out.println("  " + duplicateClassJarFile);
            } // for resource

            if (verbose) System.out.println();
        } // for duplicatePath

        System.out.println("Found duplicate resources among "
                        + ((metaInfManifestCount != 0)? metaInfManifestCount + " x META-INF/MANIFEST, " : "")
                        + ((metaInfIndexList != 0)? metaInfIndexList + " x META-INF/INDEX.LIST, " : "")
                        + ((metaInfJandexIdxCount != 0)? metaInfJandexIdxCount + " x META-INF/jandex.idx, " : "")
                        + ((metaInfOtherCount != 0)? metaInfOtherCount + " x other META-INF/**, " : "")
                        + "\n  "
                        + ((noticeCount != 0)? noticeCount + " x NOTICE, " : "")
                        + ((licenseCount != 0)? licenseCount + " x LICENSE, " : "")
                        + "\n  "
                        + ((packageInfoCount != 0)? packageInfoCount + " x package-info.class, " : "")
                        + ((moduleInfoCount != 0)? moduleInfoCount + " x module-info.class, " : "")
                        + "\n  "
                        + ((innerClassCount != 0)? innerClassCount + " x inner classes, " : "")
                        + ((unusedStubClassCount != 0)? unusedStubClassCount + " x UnusedStubClass, " : "")
                        + "\n  "
                        + ((manifestVmCount != 0)? manifestVmCount + " x manifest.vm, " : "")
                        + ((schemaValidationSchemaJsonCount != 0)? schemaValidationSchemaJsonCount + " x schema/validation-schema.json, " : "")
                        + ((schemaKubeSchemaJsonCount != 0)? schemaKubeSchemaJsonCount + " x schema/kube-schema.json, " : "")
        );
        System.out.println();

        for(val jarDuplicateStats : jarDuplicateStatsMap.values()) {
            val jarResourceMap = resourceMapByClasspathElementFileMap.get(jarDuplicateStats.resolvedJarFile);
            val duplicateJarResourceMap = resourceMapByClasspathElementFileMap.get(jarDuplicateStats.jarWithDuplicateFile);
            int jarResourceCount = (jarResourceMap != null)? jarResourceMap.size() : 0;
            int duplicateJarResourceCount = (duplicateJarResourceMap != null)? duplicateJarResourceMap.size() : 0;
            String jarFileDisplay = jarDuplicateStats.resolvedJarFile + " (with " + jarResourceCount + " resources)";
            String jarWithDuplicateFileDisplay = jarDuplicateStats.jarWithDuplicateFile + " (with " + duplicateJarResourceCount + " resources)";

            int duplicateCount = jarDuplicateStats.duplicateResources.size();
            if (duplicateCount == 1) {
                String duplicateResource = jarDuplicateStats.duplicateResources.iterator().next();
                System.out.println("Jar " + jarFileDisplay //
                        + " has 1 duplicate" //
                        + " in " + jarWithDuplicateFileDisplay //
                        + "\n"
                        + "   for resources " + duplicateResource);
            } else {
                String commonPrefixFqn = CommonPrefix.findCommonPrefix(jarDuplicateStats.duplicateResources);
                int commonPrefixLen = commonPrefixFqn.length();
                String duplicateClassesSuffixesText = jarDuplicateStats.duplicateResources.stream()
                        .map(x -> x.substring(commonPrefixLen))
                        .collect(Collectors.joining(", "));

                System.out.println("Jar " + jarFileDisplay //
                        + " has " + duplicateCount + " duplicate(s)" //
                        + " in " + jarWithDuplicateFileDisplay //
                        + "\n" //
                        + "   for resources" + ((commonPrefixLen != 0)? " with common prefix '" + commonPrefixFqn + "'" : "")//
                        + ": " + duplicateClassesSuffixesText);
            }
        }

    }

}