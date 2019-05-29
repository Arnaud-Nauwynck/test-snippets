
pomVersion=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)
jansiVersion=$(mvn -q -DforceStdout help:evaluate -Dexpression=jansi.version)
echo using pomVersion: ${pomVersion}, jansiVersion:${jansiVersion}

JVM_ARGS=""
JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote"
JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote.port=7091"
JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote.authenticate=false"
JVM_ARGS="${JVM_ARGS} -Dcom.sun.management.jmxremote.ssl=false"
JVM_ARGS="${JVM_ARGS} -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"

CLASSPATH="target/benchmark-jansi-1.0.0-SNAPSHOT.jar;target/dependency/jansi-1.18-SNAPSHOT.jar"

echo ... java ${JVM_ARGS} -cp "${CLASSPATH}" org.fusesource.jansi.BenchmarkMain 
java ${JVM_ARGS} -cp "${CLASSPATH}" org.fusesource.jansi.BenchmarkMain &

pid=$!
echo "launched pid: ${pid}"
echo ${pid} > application.pid
