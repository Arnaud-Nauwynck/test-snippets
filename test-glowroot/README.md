
This is a test for capturing some methods calls performed by an instrumentated jvm.
<br/>
It used internally a custom plugin writen with Glowroot  

<br/>
A sample usage is here to detect all file write access performed by maven in "/home" sub dirs.
</br/>

Internally, the plugin instrument all calls to "java.io.FileOutputStream.open()" and "java.io.File.renameTo()",
filter if the path starts with "/home/", and print the corresponding stack trace with the count/total of write access.


# Discover Glowroot
[https://github.com/glowroot]

Glowroot is an APM (Application Performance Management) like many other expensive closed-source tools, but it is free and Open Source..

The performance measurment offer a rich centralised server and a web UI, but this is not required. Glowroot can run as embedded server, or no server.

Moreover, glowroot is internally based on a jvm instrumentation engine, is modular by plugins, and can be used without the performance api parts.  


# Step 1: download and unzip GlowRoot agent
```
wget https://github.com/glowroot/glowroot/releases/download/v0.13.4/glowroot-0.13.4-dist.zip
unzip glowroot-0.13.4-dist.zip
```

# Step 2: compile this File instrumentation Glowroot plugin
```
mvn package
```


# Step 3: test this plugin

execute a java process, by adding jvm arguments
```
-javaagent:./glowroot/glowroot.jar -Xbootclasspath/a:./target/test-glowroot-${PROJECT_VERSION}.jar"
```


For example for instrumenting maven itself 

```
./mvn-glowroot-file.sh install
```

cf detailed script:
```
#!/bin/bash
# file: mvn-glowroot-file.sh
# script similar to "mvn", that add glowroot file write plugin instrumentation
PROJECT_VERSION=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)
PLUGIN_JAR="./target/test-glowroot-${PROJECT_VERSION}.jar"

MAVEN_OPTS="${MAVEN_OPTS} -javaagent:./glowroot/glowroot.jar"
MAVEN_OPTS="${MAVEN_OPTS} -Xbootclasspath/a:${PLUGIN_JAR}"

# MAVEN_OPTS="${MAVEN_OPTS} -Dglowroot.debug.printClassLoading=true"

export MAVEN_OPTS

mvn $@ 

```

Write access performed by maven are printed to stdout lines starting by '*****' , with the corresponding stack trace.
In this default plugin configuration, only pathes that start with '/home/' are printed, this can be changed by glooroot arguments and configuration files (-Dglowroot.conf.dir, config.json, config-default.json, ...)

Results logs:
```
2019-05-09 23:53:42.033 INFO  org.glowroot - Glowroot version: 0.13.4, built 2019-04-21 20:27:33 +0000
2019-05-09 23:53:42.035 INFO  org.glowroot - Java version: 1.8.0_131 (Oracle Corporation / Linux)
2019-05-09 23:53:42.035 INFO  org.glowroot - Java args: -javaagent:./glowroot/glowroot.jar -Xbootclasspath/a:./target/test-glowroot-0.0.1-SNAPSHOT.jar
**** cinit class fr.an.test.glowroot.FileAspect
***** (1/1) write /home/arnaud/.oracle_jre_usage/db4f55ab9af3a5a2.timestamp from stack:FileAspect$FileOutputStreamOpenAdvice.onBefore:46/FileOutputStream.open/<init>:213/<init>:162/UsageTrackerClient.registerUsage:434/setupAndTimestamp:300/access$000:78/UsageTrackerClient$4.run:322/run:317/AccessController.doPrivileged/UsageTrackerClient.run:317/PostVMInitHook.trackJavaUsage:29/run:21
2019-05-09 23:53:45.226 INFO  org.glowroot - UI listening on 127.0.0.1:4000 (to access the UI from remote machines, change the bind address to 0.0.0.0, either in the Glowroot UI under Configuration > Web or directly in the admin.json file, and then restart JVM to take effect)
[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------------< fr.an.tests:test-glowroot >----------------------
[INFO] Building test-glowroot 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ test-glowroot ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 1 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.7.0:compile (default-compile) @ test-glowroot ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ test-glowroot ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 0 resource
[INFO] 
[INFO] --- maven-compiler-plugin:3.7.0:testCompile (default-testCompile) @ test-glowroot ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ test-glowroot ---
[INFO] Tests are skipped.
[INFO] 
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ test-glowroot ---
[INFO] 
[INFO] --- maven-install-plugin:2.4:install (default-install) @ test-glowroot ---
[INFO] Installing /mnt/a_1tera2/homeData/arnaud/perso/devPerso/my-github/test-snippets.github/test-glowroot/target/test-glowroot-0.0.1-SNAPSHOT.jar to /home/arnaud/.m2/repository/fr/an/tests/test-glowroot/0.0.1-SNAPSHOT/test-glowroot-0.0.1-SNAPSHOT.jar
[INFO] Installing /mnt/a_1tera2/homeData/arnaud/perso/devPerso/my-github/test-snippets.github/test-glowroot/pom.xml to /home/arnaud/.m2/repository/fr/an/tests/test-glowroot/0.0.1-SNAPSHOT/test-glowroot-0.0.1-SNAPSHOT.pom
***** (2/5) write /home/arnaud/.m2/repository/fr/an/tests/test-glowroot/0.0.1-SNAPSHOT/test-glowroot-0.0.1-SNAPSHOT.pom from stack:FileAspect$FileOutputStreamOpenAdvice.onBefore:46/FileOutputStream.open/<init>:213/<init>:162/DefaultFileProcessor.copy:166/copy:150/DefaultInstaller.install:267/install:195/install:152/DefaultRepositorySystem.install:377/DefaultArtifactInstaller.install:107/InstallMojo.execute:115/DefaultBuildPluginManager.executeMojo:137/MojoExecutor.execute:208/execute:154/execute:146/LifecycleModuleBuilder.buildProject:117/buildProject:81/SingleThreadedBuilder.build:56/LifecycleStarter.execute:128/DefaultMaven.doExecute:305/doExecute:192/execute:105/MavenCli.execute:954/doMain:288/main:192/NativeMethodAccessorImpl.invoke0/invoke:62/DelegatingMethodAccessorImpl.invoke:43/Method.invoke:498/Launcher.launchEnhanced:289/launch:229/mainWithExitCode:415/main:356
***** (3/6) write /home/arnaud/.m2/repository/fr/an/tests/test-glowroot/0.0.1-SNAPSHOT/maven-metadata-local.xml from stack:FileAspect$FileOutputStreamOpenAdvice.onBefore:46/FileOutputStream.open/<init>:213/<init>:162/XmlStreamWriter.<init>:60/WriterFactory.newXmlWriter:122/MavenMetadata.write:116/merge:78/DefaultInstaller.install:302/install:205/install:152/DefaultRepositorySystem.install:377/DefaultArtifactInstaller.install:107/InstallMojo.execute:115/DefaultBuildPluginManager.executeMojo:137/MojoExecutor.execute:208/execute:154/execute:146/LifecycleModuleBuilder.buildProject:117/buildProject:81/SingleThreadedBuilder.build:56/LifecycleStarter.execute:128/DefaultMaven.doExecute:305/doExecute:192/execute:105/MavenCli.execute:954/doMain:288/main:192/NativeMethodAccessorImpl.invoke0/invoke:62/DelegatingMethodAccessorImpl.invoke:43/Method.invoke:498/Launcher.launchEnhanced:289/launch:229/mainWithExitCode:415/main:356
***** (4/7) write /home/arnaud/.m2/repository/fr/an/tests/test-glowroot/maven-metadata-local.xml from stack:FileAspect$FileOutputStreamOpenAdvice.onBefore:46/FileOutputStream.open/<init>:213/<init>:162/XmlStreamWriter.<init>:60/WriterFactory.newXmlWriter:122/MavenMetadata.write:116/merge:78/DefaultInstaller.install:302/install:205/install:152/DefaultRepositorySystem.install:377/DefaultArtifactInstaller.install:107/InstallMojo.execute:115/DefaultBuildPluginManager.executeMojo:137/MojoExecutor.execute:208/execute:154/execute:146/LifecycleModuleBuilder.buildProject:117/buildProject:81/SingleThreadedBuilder.build:56/LifecycleStarter.execute:128/DefaultMaven.doExecute:305/doExecute:192/execute:105/MavenCli.execute:954/doMain:288/main:192/NativeMethodAccessorImpl.invoke0/invoke:62/DelegatingMethodAccessorImpl.invoke:43/Method.invoke:498/Launcher.launchEnhanced:289/launch:229/mainWithExitCode:415/main:356
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.008 s
[INFO] Finished at: 2019-05-09T23:53:46+02:00
[INFO] ------------------------------------------------------------------------

```

Every stack trace occurence is printed horizontally (to not misconfound with an exception), for example:
```
FileAspect$FileOutputStreamOpenAdvice.onBefore:46/FileOutputStream.open/<init>:213/<init>:162/XmlStreamWriter.<init>:60/WriterFactory.newXmlWriter:122/MavenMetadata.write:116/merge:78/DefaultInstaller.install:302/install:205/install:152/DefaultRepositorySystem.install:377/DefaultArtifactInstaller.install:107/InstallMojo.execute:115/DefaultBuildPluginManager.executeMojo:137/MojoExecutor.execute:208/execute:154/execute:146/LifecycleModuleBuilder.buildProject:117/buildProject:81/SingleThreadedBuilder.build:56/LifecycleStarter.execute:128/DefaultMaven.doExecute:305/doExecute:192/execute:105/MavenCli.execute:954/doMain:288/main:192/NativeMethodAccessorImpl.invoke0/invoke:62/DelegatingMethodAccessorImpl.invoke:43/Method.invoke:498/Launcher.launchEnhanced:289/launch:229/mainWithExitCode:415/main:356
```

The print format for a stack trace element is "<className>.<methodName>:<lineNumber>/" or simply "<methodName>:<lineNumber>/" to avoid repeating the same className as the one before.
Moreover, only the short classname are printed... it is pretty easy to find out the fully qualified name for the short name.

The equivalent stack trace vertically is ..
```
FileAspect$FileOutputStreamOpenAdvice.onBefore:46
FileOutputStream.open
                .<init>:213
                .<init>:162
XmlStreamWriter.<init>:60
WriterFactory.newXmlWriter:122
MavenMetadata.write:116
             .merge:78
DefaultInstaller.install:302
                .install:205
                .install:152
DefaultRepositorySystem.install:377
DefaultArtifactInstaller.install:107
InstallMojo.execute:115
DefaultBuildPluginManager.executeMojo:137
MojoExecutor.execute:208
            .execute:154
            .execute:146
LifecycleModuleBuilder.buildProject:117
                      .buildProject:81
SingleThreadedBuilder.build:56
LifecycleStarter.execute:128
DefaultMaven.doExecute:305
            .doExecute:192
            .execute:105
MavenCli.execute:954
MavenCli.doMain:288
MavenCli.main:192
NativeMethodAccessorImpl.invoke0
NativeMethodAccessorImpl.invoke:62
DelegatingMethodAccessorImpl.invoke:43
Method.invoke:498
Launcher.launchEnhanced:289
        .launch:229
        .mainWithExitCode:415
        .main:356
```



# Conclusion

Have fun with Glowroot ! Write your own plugins


