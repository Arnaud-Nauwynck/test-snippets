
Simple test for using a custom Hadoop FileSystem implementation class
... backed by a pure java.io local directory on windows

The built-in class in Hadoop org.apache.hadoop.fs.RawLocalFileSystem does not work if 
you do not install the native winutils.exe 

conf/core-site.xml : 

<?xml version="1.0" encoding="UTF-8"?>
<configuration>

  <property>
    <name>fs.defaultFS</name>
    <value>fs1:///D:/arn/hadoop/rootfs</value>
  </property>

  <property>
    <name>fs.fs1.impl</name>
    <value>fr.an.tests.hadoopfs.JavaioFileSystem</value>
  </property>
  

</configuration>


Testing with debugger of GlowRoot instrumentation agent... 
using GlowRoot
set HADOOP_OPTS=
set HADOOP_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
set HADOOP_OPTS=-javaagent:glowroot.jar -Xbootclasspath/a:d:/arn/devPerso/mygithub/test-snippets/test-glowroot/target/test-glowroot-0.0.1-SNAPSHOT.jar


