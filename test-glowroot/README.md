
This is a test for capturing some methods calls performed by an instrumentated jvm.
<br/>
It used internally a custom plugin writen with Glowroot  

<br/>
A sample usage is here to detect all file write access performed by maven in "/home" sub dirs.
</br/>

Internally, the plugin instrument all calls to "java.io.FileOutputStream.open(" and "java.io.File.renameTo()",
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

The print format for a stack trace element is 
    "className.methodName:lineNumber/" 
or simply 
    "methodName:lineNumber/" to avoid repeating the same className as the one before.
Moreover, only the short classname are printed... it is pretty easy to find out the fully qualified name for the short name.

The equivalent stack trace vertically is ..
```
FileAspect$FileOutputStreamOpenAdvice.onBefore:46
FileOutputStream.open/<init>:213/<init>:162
XmlStreamWriter.<init>:60
WriterFactory.newXmlWriter:122
MavenMetadata.write:116/merge:78
DefaultInstaller.install:302/install:205/install:152
DefaultRepositorySystem.install:377
DefaultArtifactInstaller.install:107
InstallMojo.execute:115
DefaultBuildPluginManager.executeMojo:137
MojoExecutor.execute:208/execute:154/execute:146
LifecycleModuleBuilder.buildProject:117/buildProject:81
SingleThreadedBuilder.build:56
LifecycleStarter.execute:128
DefaultMaven.doExecute:305/doExecute:192/execute:105
MavenCli.execute:954
MavenCli.doMain:288
MavenCli.main:192
NativeMethodAccessorImpl.invoke0
NativeMethodAccessorImpl.invoke:62
DelegatingMethodAccessorImpl.invoke:43
Method.invoke:498
Launcher.launchEnhanced:289/launch:229/mainWithExitCode:415/main:356
```


# Test again .. log stacktrace for files downloaded and written to local repo

Here is another test execution, to detect the files downloaded by maven and written to local repository


```
rm -rf ~/.m2/repository/org/glowroot/glowroot-agent-plugin-api/
./mvn-glowroot-file.sh package
```

Results:
written files:
- .pom.part
- .pom.sha1-$(uuid).tmp
- .pom         (.pom.part renamedTo)
- .pom.sha1    (.pom.sha1..tmp renamedTo)
- .jar.part
- .jar.sha1-$(uuid).tmp 
- .jar         (.jar.part renamedTo)
- .jar.sha1    (.pom.sha1 renamedTo)


All following stack traces have in common this stack fragment, ommited next

```
    MojoExecutor.execute:202/execute:156/execute:148
    LifecycleModuleBuilder.buildProject:117/buildProject:81
    SingleThreadedBuilder.build:56
    LifecycleStarter.execute:128
    DefaultMaven.doExecute:305/doExecute:192/execute:105
    MavenCli.execute:956/doMain:288/main:192
    NativeMethodAccessorImpl.invoke0:62
    DelegatingMethodAccessorImpl.invoke:43
    Method.invoke:498
    Launcher.launchEnhanced:289/launch:229/mainWithExitCode:415/main:356
```




```
..

Downloading from central: https://repo.maven.apache.org/maven2/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.pom
***** FileOutputStream.open '..\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.part 
from stack:
    FileOutputStream.open/<init>:213/<init>:162
    LazyFileOutputStream.initialize:154
    LazyFileOutputStream.write:126
    AbstractWagon.transfer:581/getTransfer:372/getTransfer:315/getTransfer:284
    StreamWagon.getIfNewer:97/get:61
    WagonTransporter$GetTaskRunner.run:567
    WagonTransporter.execute:435/get:412
    BasicRepositoryConnector$GetTaskRunner.runTask:456
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215/resolveArtifact:192
    DefaultArtifactDescriptorReader.loadPom:240/readArtifactDescriptor:171
    DefaultDependencyCollector.resolveCachedArtifactDescriptor:530/getArtifactDescriptorResult:513/
        processDependency:402/processDependency:356/process:344/collectDependencies:247
    DefaultRepositorySystem.collectDependencies:269
    DefaultProjectDependenciesResolver.resolve:169
    LifecycleDependencyResolver.getDependencies:243/resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248
        
***** FileOutputStream.open '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.sha1-77ce64e42112642211906003866.tmp 
from stack:
    FileOutputStream.open:213/<init>:162
    LazyFileOutputStream.initialize:154/write:126
    AbstractWagon.transfer:581/getTransfer:372/getTransfer:315/getTransfer:284
    StreamWagon.getIfNewer:97/get:61
    WagonTransporter$GetTaskRunner.run:567
    WagonTransporter.execute:435/get:412
    BasicRepositoryConnector$GetTaskRunner.fetchChecksum:423
    ChecksumValidator.validateExternalChecksums:157/validate:103
    BasicRepositoryConnector$GetTaskRunner.runTask:459
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215/resolveArtifact:192
    DefaultArtifactDescriptorReader.loadPom:240/readArtifactDescriptor:171
    DefaultDependencyCollector.resolveCachedArtifactDescriptor:530/getArtifactDescriptorResult:513/
        processDependency:402/processDependency:356/process:344/collectDependencies:247
    DefaultRepositorySystem.collectDependencies:269
    DefaultProjectDependenciesResolver.resolve:169
    LifecycleDependencyResolver.getDependencies:243/resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248

***** (1/1) File.renameTo() dest: '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom'
    src:  '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.part' 
from stack:
    File.renameTo
    DefaultFileProcessor.move:249
    BasicRepositoryConnector$GetTaskRunner.runTask:481
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215/resolveArtifact:192
    DefaultArtifactDescriptorReader.loadPom:240/readArtifactDescriptor:171
    DefaultDependencyCollector.resolveCachedArtifactDescriptor:530/getArtifactDescriptorResult:513
        /processDependency:402/processDependency:356/process:344
    DefaultDependencyCollector.collectDependencies:247
    DefaultRepositorySystem.collectDependencies:269
    ProjectDependenciesResolver.resolve:169
    LifecycleDependencyResolver.getDependencies:243
    LifecycleDependencyResolver.resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248

***** (2/2) File.renameTo() dest: '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.sha1' 
    src:  '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.sha1-ad24d7b01489881715896332211.tmp'
from stack:
    File.renameTo
    DefaultFileProcessor.move:249
    ChecksumValidator.commit:244
    BasicRepositoryConnector$GetTaskRunner.runTask:484
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215/resolveArtifact:192
    DefaultArtifactDescriptorReader.loadPom:240/readArtifactDescriptor:171
    DefaultDependencyCollector.resolveCachedArtifactDescriptor:530/getArtifactDescriptorResult:513
        /processDependency:402/processDependency:356/process:344/collectDependencies:247
    DefaultRepositorySystem.collectDependencies:269
    ProjectectDependenciesResolver.resolve:169
    LifecycleDependencyResolver.getDependencies:243
    LifecycleDependencyResolver.resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248

Downloaded from central: https://repo.maven.apache.org/maven2/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.pom : https://repo.maven.apache.org/maven2/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.jar

***** FileOutputStream.open() ~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.jar.part 
from stack:
    FileOutputStream.open:213/<init>:162
    LazyFileOutputStream.initialize:154/write:126
    AbstractWagon.transfer:581/getTransfer:372/getTransfer:315/getTransfer:284
    StreamWagon.getIfNewer:97/get:61
    WagonTransporter$GetTaskRunner.run:567
    WagonTransporter.execute:435/get:412
    BasicRepositoryConnector$GetTaskRunner.runTask:456
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215
    DefaultRepositorySystem.resolveDependencies:325
    DefaultProjectDependenciesResolver.resolve:202
    LifecycleDependencyResolver.getDependencies:243/resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248
    
***** FileOutputStream.open() '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.jar.sha1-cebc165f7113521320898028675.tmp' 
from stack:
    FileOutputStream.open:213/<init>:162
    LazyFileOutputStream.initialize:154/write:126
    AbstractWagon.transfer:581/getTransfer:372/getTransfer:315/getTransfer:284
    StreamWagon.getIfNewer:97/get:61
    WagonTransporter$GetTaskRunner.run:567
    WagonTransporter.execute:435/get:412
    BasicRepositoryConnector$GetTaskRunner.fetchChecksum:423
    ChecksumValidator.validateExternalChecksums:157
    ChecksumValidator.validate:103
    BasicRepositoryConnector$GetTaskRunner.runTask:459
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215
    DefaultRepositorySystem.resolveDependencies:325
    DefaultProjectDependenciesResolver.resolve:202
    LifecycleDependencyResolver.getDependencies:243/resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248
    
***** File.renameTo() '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.jar' 
    src: '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.jar.part'
from stack:
    File.renameTo
    DefaultFileProcessor.move:249
    BasicRepositoryConnector$GetTaskRunner.runTask:481
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215
    DefaultRepositorySystem.resolveDependencies:325
    DefaultProjectDependenciesResolver.resolve:202
    LifecycleDependencyResolver.getDependencies:243/resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248)

***** File.renameTo() dest: '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.jar.sha1'
    src: '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.jar.sha1-5c65fc31805141540408522583.tmp'
from stack:
    File.renameTo
    DefaultFileProcessor.move:249
    ChecksumValidator.commit:244
    BasicRepositoryConnector$GetTaskRunner.runTask:484
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215
    DefaultRepositorySystem.resolveDependencies:325
    DefaultProjectDependenciesResolver.resolve:202
    LifecycleDependencyResolver.getDependencies:243/resolveProjectDependencies:147
    MojoExecutor.ensureDependenciesAreResolved:248
    
```


# Test again .. downloaded invalid checkSum files, with default checkSumPolicy=warn

I am using here a dummy http repository server, that always answer http 200, with a welcome html page. Therefore, all pom, jar and sh1 files are invalid.


For this, I use an additionnal maven profile "-Pdummy-repo"

In the ~/.m2/settings.xml:
```xml
    <profile>
        <id>dummy-repo</id>
        <repositories>
            <repository>
                <id>central</id>
                <url>http://localhost:8090/repo</url>
            </repository>
        </repositories>
        <pluginRepositories>
            <pluginRepository>
                <id>central</id>
                <url>http://localhost:8090/repo</url>
            </pluginRepository>
        </pluginRepositories>
    </profile>
```


```
rm -rf ~/.m2/repository/org/glowroot/glowroot-agent-plugin-api/
./mvn-glowroot-file.sh -Pdummy-repo package
```

Now, we can see maven is still downloading pom.part, pom.sha1, and then does renameTo the same way !!!
By debuggin it, this is becasue by default, repositories have checkSumPolicy="warn", instead of "fail"

```
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.pom

[WARNING] Could not validate integrity of download from http://localhost:8090/repo/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.pom: Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd
                   
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.pom

***** (1/1) File.renameTo() '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom'
    src:'~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.part'
from stack:
    File.renameTo
    DefaultFileProcessor.move:249
    BasicRepositoryConnector$GetTaskRunner.runTask:481
    BasicRepositoryConnector$TaskRunner.run:363)
    RunnableErrorForwarder$1.run:75)
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215/resolveArtifact:192
    DefaultArtifactDescriptorReader.loadPom:240/readArtifactDescriptor:171
    DefaultDependencyCollector.resolveCachedArtifactDescriptor:530


***** (2/2) File.renameTo() '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.sha1' 
    src:'~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.sha1-1461f17f5117003776646361438.tmp'
from stack:
    File.renameTo
    DefaultFileProcessor.move:249
    ChecksumValidator.commit:244
    BasicRepositoryConnector$GetTaskRunner.runTask:484
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489
    DefaultArtifactResolver.resolve:390
```


# Test again .. downloaded invalid checkSum files, with forced checkSumPolicy=fail

For this, I now use maven profile "-Pdummy-repo-fail"

In the ~/.m2/settings.xml:
```xml
    <profile>
        <id>dummy-repo-fail</id>
        <repositories>
            <repository>
                <id>central</id>
                <url>http://localhost:8090/repo</url>
                <releases>
                    <checksumPolicy>fail</checksumPolicy>
                </releases>
                <snapshots>
                    <checksumPolicy>fail</checksumPolicy>
                </snapshots>
            </repository>
        </repositories>
        <pluginRepositories>
            <pluginRepository>
                <id>central</id>
                <url>http://localhost:8090/repo</url>
                <releases>
                    <checksumPolicy>fail</checksumPolicy>
                </releases>
                <snapshots>
                    <checksumPolicy>fail</checksumPolicy>
                </snapshots>
            </pluginRepository>
        </pluginRepositories>
    </profile>
```


```
rm -rf ~/.m2/repository/org/glowroot/glowroot-agent-plugin-api/
./mvn-glowroot-file.sh -Pdummy-repo-fail package
```

Now maven correctly detect a build failure
```
Downloading from central: http://localhost:8090/repo/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.pom
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/org/glowroot/glowroot-agent-plugin-api/0.13.4/glowroot-agent-plugin-api-0.13.4.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.224 s
[INFO] Finished at: 2019-05-11T15:42:35+02:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal on project test-glowroot: Could not resolve dependencies for project fr.an.tests:test-glowroot:jar:0.0.1-SNAPSHOT: Failed to collect dependencies at org.glowroot:glowroot-agent-plugin-api:jar:0.13.4: Failed to read artifact descriptor for org.glowroot:glowroot-agent-plugin-api:jar:0.13.4: Could not transfer artifact org.glowroot:glowroot-agent-plugin-api:pom:0.13.4 from/to central (http://localhost:8090/repo): Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd -> [Help 1]
```

And we can check that only ".part" or ".tmp" files are written, and they are deleted correctly.

```
***** (2/2) File.delete() '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.part' from stack:
    at java.io.File.delete:342)
    BasicRepositoryConnector$GetTaskRunner.runTask:489
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215/resolveArtifact:192
    DefaultArtifactDescriptorReader.loadPom:240/readArtifactDescriptor:171
    DefaultDependencyCollector.collectDependencies:247

***** (3/3 File.delete() '~\.m2\repository\org\glowroot\glowroot-agent-plugin-api\0.13.4\glowroot-agent-plugin-api-0.13.4.pom.part.lock' from stack:
    File.delete
    PartialFile$LockFile.close:236
    PartialFile.close:349
    BasicRepositoryConnector$GetTaskRunner.runTask:489
    BasicRepositoryConnector$TaskRunner.run:363
    RunnableErrorForwarder$1.run:75
    BasicRepositoryConnector$DirectExecutor.execute:642
    BasicRepositoryConnector.get:262
    DefaultArtifactResolver.performDownloads:489/resolve:390/resolveArtifacts:215/resolveArtifact:192
    DefaultArtifactDescriptorReader.loadPom:240/readArtifactDescriptor:171
    DefaultDependencyCollector.collectDependencies:247

```



# Conclusion

Have fun with Glowroot ! Write your own plugins


