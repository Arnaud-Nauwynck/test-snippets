
This dummy http server answers "http 200" with a dummy html page for every file request


## Introduction


The goal is to demonstrate maven local repository corruption


This occurs in a (badly configured) network where you are supposed to use a http proxy explicitely, but the remaining http traffic is redirected to a gateway, showing a login html page

This will corrupt maven repository with html page instead of pom/jar files.

Notice you could use the strict signature check mode:
	"mvn -C .. ", 
that also download ".asc" file

Setup in your settings.xml:
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


Then start the http server 
```
mvn install spring-boot:run
```

browse any url on on http://localhost:8090/repo/*
http://localhost:8090/repo/dummy/file.pom


## Test downloading plugin 'dummy-maven-plugin'


```
rm -rf ~/.m2/repository/org/apache/maven/plugins/dummy-maven-plugin; 
mvn -Pdummy-repo org.apache.maven.plugins:dummy-maven-plugin:1.0 .0:dummy-goal
```


Here is the result log:

```
[INFO] Scanning for projects...                                                                                                                              
Downloading from central: http://localhost:8090/repo/org/apache/maven/plugins/dummy-maven-plugin/1.0.0/dummy-maven-plugin-1.0.0.pom                          
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/org/apac
aven/plugins/dummy-maven-plugin/1.0.0/dummy-maven-plugin-1.0.0.pom                                                                                           
[WARNING] Could not validate integrity of download from http://localhost:8090/repo/org/apache/maven/plugins/dummy-maven-plugin/1.0.0/dummy-maven-plugin-1.0.0
: Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd                                                             
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/org/apac
aven/plugins/dummy-maven-plugin/1.0.0/dummy-maven-plugin-1.0.0.pom                                                                                           
Downloaded from central: http://localhost:8090/repo/org/apache/maven/plugins/dummy-maven-plugin/1.0.0/dummy-maven-plugin-1.0.0.pom (392 B at 1.1 kB/s)       
[WARNING] The POM for org.apache.maven.plugins:dummy-maven-plugin:jar:1.0.0 is invalid, transitive dependencies (if any) will not be available, enable debug 
ing for more details                                                                                                                                         
[INFO] ------------------------------------------------------------------------                                                                              
[INFO] BUILD FAILURE                                                                                                                                         
[INFO] ------------------------------------------------------------------------                                                                              
[INFO] Total time:  1.682 s                                                                                                                                  
[INFO] Finished at: 2019-05-01T18:15:51+02:00                                                                                                                
[INFO] ------------------------------------------------------------------------                                                                              
[ERROR] Plugin org.apache.maven.plugins:dummy-maven-plugin:1.0.0 or one of its dependencies could not be resolved: Failed to read artifact descriptor for org
che.maven.plugins:dummy-maven-plugin:jar:1.0.0: 1 problem was encountered while building the effective model                                                 
[ERROR] [FATAL] Non-parseable POM C:\Users\arnau\.m2\repository\org\apache\maven\plugins\dummy-maven-plugin\1.0.0\dummy-maven-plugin-1.0.0.pom: end tag name 
dy> must match start tag name <br> from line 10 (position: TEXT seen ...<br>\r\n    \r\n</body>... @12:8)  @ line 12, column 8                               
[ERROR] -> [Help 1]                                                                                                                                          
[ERROR]                                                                                                                                                      
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.                                                                          
[ERROR] Re-run Maven using the -X switch to enable full debug logging.                                                                                       
[ERROR]                                                                                                                                                      
[ERROR] For more information about the errors and possible solutions, please read the following articles:                                                    
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/PluginResolutionException                                                                  
```


Despite maven detected it was corrupted, it stored it in its local repository (but not the jar):

```
$ ls -1 ~/.m2/repository/org/apache/maven/plugins/dummy-maven-plugin/1.0.0/
_remote.repositories
dummy-maven-plugin-1.0.0.pom
dummy-maven-plugin-1.0.0.pom.sha1

$ cat ~/.m2/repository/org/apache/maven/plugins/dummy-maven-plugin/1.0.0/dummy-maven-plugin-1.0.0.pom
..
```


## Test downloading pom.xml + jar dependency 'dummy-dependency' 

Here is an invalid pom.xml in src/test/dummy-mvn-project

```
$ cd src/test/dummy-mvn-project
$ rm -rf ~/.m2/repository/fr/an/tests/dummy-dependency/1.0
$ mvn -Pdummy-repo install

[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------< fr.an.tests:dummy-project >----------------------
[INFO] Building dummy-project 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
Downloading from central: http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.pom
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.pom
[WARNING] Could not validate integrity of download from http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.pom: Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.pom
Downloaded from central: http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.pom (392 B at 1.9 kB/s)
[WARNING] The POM for fr.an.tests:dummy-dpendency:jar:1.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more details
Downloading from central: http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.jar
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.jar
[WARNING] Could not validate integrity of download from http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.jar: Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd
[WARNING] Checksum validation failed, expected <!DOCTYPE but is 28db9960ddfec8ed449bb87ec4d17c139d9301fd from central for http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.jar
Downloaded from central: http://localhost:8090/repo/fr/an/tests/dummy-dpendency/1.0/dummy-dpendency-1.0.jar (392 B at 3.5 kB/s)

...

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  10.822 s
[INFO] Finished at: 2019-05-01T19:32:27+02:00
[INFO] ------------------------------------------------------------------------
```

The dummy dependency is loaded, and both the pom and jar files are stored invalid:

```
$ ls -l ~/.m2/repository/fr/an/tests/dummy-dependency/1.0/
total 5
-rwx------+ 1 arnaud Aucun 213  1 mai   20:10 _remote.repositories
-rwx------+ 1 arnaud Aucun 392  1 mai   20:10 dummy-dependency-1.0.jar
-rwx------+ 1 arnaud Aucun 392  1 mai   20:10 dummy-dependency-1.0.jar.sha1
-rwx------+ 1 arnaud Aucun 392  1 mai   20:10 dummy-dependency-1.0.pom
-rwx------+ 1 arnaud Aucun 392  1 mai   20:10 dummy-dependency-1.0.pom.sha1
```

## Recompiling with corrupted local maven repository files

```
$ mvn install                                                                                                                                                     
[INFO] Scanning for projects...                                                                                                                                  
[INFO]                                                                                                                                                           
[INFO] ---------------------< fr.an.tests:dummy-project >----------------------                                                                                  
[INFO] Building dummy-project 1.0-SNAPSHOT                                                                                                                       
[INFO] --------------------------------[ jar ]---------------------------------                                                                                  
[WARNING] The POM for fr.an.tests:dummy-dependency:jar:1.0 is invalid, transitive dependencies (if any) will not be available, enable debug logging for more deta
ils                                                                                                                                                              
[INFO]                                                                                                                                                           
...

[INFO] Installing D:\arn\mvn\dummy-mvn-project\pom.xml to C:\Users\arnau\.m2\repository\fr\an\tests\dummy-project\1.0-SNAPSHOT\dummy-project-1.0-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.812 s
[INFO] Finished at: 2019-05-01T20:43:54+02:00
[INFO] ------------------------------------------------------------------------

```
Maven does not try to re-download the invalid files stored... But it logs that the pom files are indeed invalid  (not the jar file!) 
Notice that it does not cause a "build failure" ... if the dependency is not critical, you still have "build success"!

In case your downloaded jar files are mandatory for your compilation, then you would have incomprehensible "compiler error - class not found" in javac, but the real error is only logged


## Conclusion

When you network gateway is badly configured, you can corrupt your maven local repository, with invalid pom.xml + jar file of dependency and invalid pom.xml of plugin.

Maven detects and logs this errors, but does not help in fixing the errors!
You have to carefully read all the logs (in particular at the beginning of logs)

