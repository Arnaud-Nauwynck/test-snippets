

# Test for deploy in 2 phases: staging to local dir, then upload

## Step 1: deploy to staging local dir



```
mvn deploy -DaltDeploymentRepository=local::default::file:./target/staging-deploy
```

logs:
```
[INFO] --- maven-deploy-plugin:2.7:deploy (default-deploy) @ dummy-project ---
[INFO] Using alternate deployment repository local::default::file:./target/staging-deploy
Downloading from local: file:./target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/maven-metadata.xml
Uploading to local: file:./target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.jar
Uploaded to local: file:./target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.jar (2.4 kB at 26 kB/s)
Uploading to local: file:./target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.pom
Uploaded to local: file:./target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.pom (709 B at 13 kB/s)
Downloading from local: file:./target/staging-deploy/fr/an/tests/dummy-project/maven-metadata.xml
Uploading to local: file:./target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/maven-metadata.xml
Uploaded to local: file:./target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/maven-metadata.xml (774 B at 15 kB/s)
Uploading to local: file:./target/staging-deploy/fr/an/tests/dummy-project/maven-metadata.xml
Uploaded to local: file:./target/staging-deploy/fr/an/tests/dummy-project/maven-metadata.xml (284 B at 6.2 kB/s)
```



## Step 2: merge-maven-repos from local dir to explicit url

```
mvn wagon:merge-maven-repos -Dwagon.source=file:target/staging-deploy -Dwagon.target=http://localhost:8090/repo
```

logs
```
[INFO] --- wagon-maven-plugin:2.0.0:merge-maven-repos (default-cli) @ dummy-project ---
[INFO] Scanning remote file system: file:target/staging-deploy ...
[INFO] Downloading file:target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.jar to C:\cygwin64\tmp\wagon-maven-plugin6734280554836310756974549882126800\fr\an\tests\dummy-project\0.0.1-SNAPSHOT\dummy-project-0.0.1-20190502.200139-1.jar ...

... (truncated)

[INFO] Downloading file:target/staging-deploy/fr/an/tests/dummy-project/maven-metadata.xml.md5 to C:\cygwin64\tmp\wagon-maven-plugin6734280554836310756974549882126800\fr\an\tests\dummy-project\maven-metadata.xml.md5 ...
[INFO] Downloading file:target/staging-deploy/fr/an/tests/dummy-project/maven-metadata.xml.sha1 to C:\cygwin64\tmp\wagon-maven-plugin6734280554836310756974549882126800\fr\an\tests\dummy-project\maven-metadata.xml.sha1 ...

[INFO] Uploading C:\cygwin64\tmp\wagon-maven-plugin6734280554836310756974549882126800\fr\an\tests\dummy-project\0.0.1-SNAPSHOT\dummy-project-0.0.1-20190502.200139-1.jar to http://localhost:8090/repo/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.jar ...

... (truncated)

[INFO] Uploading C:\cygwin64\tmp\wagon-maven-plugin6734280554836310756974549882126800\fr\an\tests\dummy-project\maven-metadata.xml.sha1 to http://localhost:8090/repo/fr/an/tests/dummy-project/maven-metadata.xml.sha1 ...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```


## Step 3: merge-maven-repos from local dir to pom distributionManagment url

The goal is to use the url extracted from the pom.xml distributionManagement..
```xml
  <distributionManagement>
  	<repository>
  		<id>test-release-repo</id>
  		<url>http://localhost:8090/repo</url>
  	</repository>
  	<snapshotRepository>
  		<id>test-snapshot-repo</id>
  		<url>http://localhost:8090/repo</url>
  		<uniqueVersion>false</uniqueVersion>
  	</snapshotRepository>
  </distributionManagement>
```

When using value '-Dwagon.target=pom' the url would be computed from the pom, which resolve either to 'distributionManagement.repository.url' (for releases) or 'distributionManagement.snapshotRepository.url' for SNAPSHOTS.

Patch for wagon-maven-plugin:
```
$ git diff                                                                                                                                              
diff --git a/src/main/java/org/codehaus/mojo/wagon/AbstractCopyMojo.java b/src/main/java/org/codehaus/mojo/wagon/AbstractCopyMojo.java                  
index 653c08d..2eb11cc 100644                                                                                                                           
--- a/src/main/java/org/codehaus/mojo/wagon/AbstractCopyMojo.java                                                                                       
+++ b/src/main/java/org/codehaus/mojo/wagon/AbstractCopyMojo.java                                                                                       
@@ -21,6 +21,8 @@ package org.codehaus.mojo.wagon;                                                                                                      
                                                                                                                                                        
 import java.io.IOException;                                                                                                                            
                                                                                                                                                        
+import org.apache.maven.model.DeploymentRepository;                                                                                                    
+import org.apache.maven.model.DistributionManagement;                                                                                                  
 import org.apache.maven.plugin.MojoExecutionException;                                                                                                 
 import org.apache.maven.wagon.ConnectionException;                                                                                                     
 import org.apache.maven.wagon.Wagon;                                                                                                                   
@@ -50,7 +52,7 @@ public abstract class AbstractCopyMojo                                                                                                
         try                                                                                                                                            
         {                                                                                                                                              
             srcWagon = createWagon( sourceId, source );                                                                                                
-            targetWagon = createWagon( targetId, target );                                                                                             
+            targetWagon = createTargetWagon();                                                                                                         
             copy( srcWagon, targetWagon );                                                                                                             
         }                                                                                                                                              
         catch ( Exception e )                                                                                                                          
@@ -65,6 +67,32 @@ public abstract class AbstractCopyMojo                                                                                               
                                                                                                                                                        
     }                                                                                                                                                  
                                                                                                                                                        
+       protected Wagon createTargetWagon() throws MojoExecutionException {                                                                             
+               String resolvedTargetId = targetId;                                                                                                     
+               String resolvedTarget = target;                                                                                                         
+               if ( target.contentEquals("pom") )                                                                                                      
+               {                                                                                                                                       
+                       DistributionManagement dist = project.getDistributionManagement();                                                              
+                       if ( dist == null )                                                                                                             
+                       {                                                                                                                               
+                               throw new MojoExecutionException("no <distributionManagement> set for using -Dmaven.target=pom");                       
+                       }                                                                                                                               
+                                                                                                                                                       
+                       boolean isSnapshot = project.getVersion().endsWith("-SNAPSHOT");                                                                
+                       DeploymentRepository repo = ( isSnapshot )? dist.getSnapshotRepository() : dist.getRepository();                                
+                                                                                                                                                       
+
+                       resolvedTargetId = repo.getId();
+                       resolvedTarget = repo.getUrl();                                                                                                 
+                       if ( resolvedTarget == null )                                                                                                   
+                       {                                                                                                                               
+                               String repoTag = ( isSnapshot )? "snapshotRepository" : "repository";                                                   
+                               throw new MojoExecutionException("no <distributionManagement><" + repoTag + "><url> set for using -Dmaven.target=pom"); 
+                       }                                                                                                                               
+                       getLog().info("resolving -Dmaven.target=pom to url=" + resolvedTarget);                                                         
+               }                                                                                                                                       
+               Wagon targetWagon = createWagon( resolvedTargetId, resolvedTarget );                                                                    
+               return targetWagon;                                                                                                                     
+       }                                                                                                                                               
+                                                                                                                                                       
     private void disconnectWagon( Wagon wagon )                                                                                                        
     {                                                                                                                                                  
         try                                                                                                                                            
```                                                                                                                                                        


Testing with patched plugin  (use explicit modified plugin version 2.0.1-SNAPSHOT):

```
mvn org.codehaus.mojo:wagon-maven-plugin:2.0.1-SNAPSHOT:merge-maven-repos -Dwagon.source=file:target/staging-deploy -Dwagon.target=pom
```

logs:
```
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------< fr.an.tests:dummy-project >----------------------
[INFO] Building dummy-project 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- wagon-maven-plugin:2.0.1-SNAPSHOT:merge-maven-repos (default-cli) @ dummy-project ---
[INFO] resolving -Dmaven.target=pom to url=http://localhost:8090/repo
[INFO] Scanning remote file system: file:target/staging-deploy ...

... (truncated)

[INFO] Uploading C:\cygwin64\tmp\wagon-maven-plugin3905000358405571560976418314482900\fr\an\tests\dummy-project\maven-metadata.xml.sha1 to http://localhost:8090/repo/fr/an/tests/dummy-project/maven-metadata.xml.sha1 ...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

## Step 4: deploy from local dir (to pom distributionManagment url)

Instead of "merge-maven-repos -Dwagon.target=pom" that use a magic value "pom", let's patch again to create a new mojo called "repository-deploy"

```
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.Wagon;
import org.codehaus.mojo.wagon.shared.MavenRepoMerger;
import org.codehaus.mojo.wagon.shared.WagonUtils;

/**
 * Deploy artifacts from a local repository to pom distributionManagement repository.
 * <p>
 * This can be used typically after 
 * <PRE>
 * mvn deploy -DaltDeploymentRepository=local::default::file:./target/staging-deploy
 * </PRE>
 */
@Mojo( name = "repository-deploy" , requiresProject = true)
public class RepositoryDeployMojo
    extends AbstractWagonMojo
{

    /**
     * The directory of the source repository.
     */
    @Parameter( property = "source", required = true)
    protected String source;

    /**
     * Optimize the upload by locally compressed all files in one bundle, upload the bundle, and finally remote
     * uncompress the bundle. This only works with SCP's URL
     */
    @Parameter( property = "wagon.optimize", defaultValue = "false")
    protected boolean optimize = false;

    @Component
    private MavenRepoMerger mavenRepoMerger;

    @Override
    public void execute()
        throws MojoExecutionException
    {
        if ( this.skip )
        {
            this.getLog().info( "Skip execution." );
            return;
        }

        Wagon srcWagon = null;
        Wagon targetWagon = null;
        try
        {
            srcWagon = createWagon( "default", "file:" + source );
            targetWagon = WagonUtils.createDistributionManagementWagon( wagonFactory, project, settings );

            mavenRepoMerger.merge( srcWagon, targetWagon, optimize, this.getLog() );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Error during performing repository deploy", e );
        }
        finally
        {
            WagonUtils.disconnectWagon( getLog(), srcWagon );
            WagonUtils.disconnectWagon( getLog(), targetWagon );
        }
    }

}
```

You can use it like this:
```
mvn org.codehaus.mojo:wagon-maven-plugin:2.0.1-SNAPSHOT:repository-deploy -Dsource=target/staging-deploy
```

logs:
```
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------< fr.an.tests:dummy-project >----------------------
[INFO] Building dummy-project 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- wagon-maven-plugin:2.0.1-SNAPSHOT:repository-deploy (default-cli) @ dummy-project ---
[INFO] Scanning remote file system: file:target/staging-deploy ...
[INFO] Downloading file:target/staging-deploy/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.jar to C:\cygwin64\tmp\wagon-maven-plugin41997402129764713611234190411700800\fr\an\tests\dummy-project\0.0.1-SNAPSHOT\dummy-project-0.0.1-20190502.200139-1.jar ...

... (truncated)

[INFO] Downloading file:target/staging-deploy/fr/an/tests/dummy-project/maven-metadata.xml.sha1 to C:\cygwin64\tmp\wagon-maven-plugin41997402129764713611234190411700800\fr\an\tests\dummy-project\maven-metadata.xml.sha1 ...
[INFO] Uploading C:\cygwin64\tmp\wagon-maven-plugin41997402129764713611234190411700800\fr\an\tests\dummy-project\0.0.1-SNAPSHOT\dummy-project-0.0.1-20190502.200139-1.jar to http://localhost:8090/repo/fr/an/tests/dummy-project/0.0.1-SNAPSHOT/dummy-project-0.0.1-20190502.200139-1.jar ...

... (truncated)

[INFO] Uploading C:\cygwin64\tmp\wagon-maven-plugin41997402129764713611234190411700800\fr\an\tests\dummy-project\maven-metadata.xml.sha1 to http://localhost:8090/repo/fr/an/tests/dummy-project/maven-metadata.xml.sha1 ...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```


