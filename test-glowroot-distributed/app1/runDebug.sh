
#
# INFO  org.glowroot - gRPC listening on 0.0.0.0:8181
# INFO  org.glowroot - UI listening on 0.0.0.0:4000
#

# pomVersion=$(mvn help:effective-pom -Dexpression=project.version -q -DforceStdout)
pomVersion=0.0.1-SNAPSHOT
echo "using pomVersion: ${pomVersion}"

jarApp=target/test-glowroot-app1-${pomVersion}.jar

jvmOpts=-javaagent:../glowroot-jvmagent/glowroot/glowroot.jar
jvmOpts="${jvmOpts} -Dglowroot.conf.dir=./glowroot/conf"
# jvmOpts="${jvmOpts} -Dglowroot.collector.address=localhost:8181"

jvmOpts="${jvmOpts} -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

echo "... java ${jvmOpts} -jar ${jarApp}"
java ${jvmOpts} -jar ${jarApp}
