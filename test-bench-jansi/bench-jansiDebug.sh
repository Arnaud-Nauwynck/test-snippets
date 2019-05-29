pomVersion=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)
jansiVersion=$(mvn -q -DforceStdout help:evaluate -Dexpression=jansi.version)
echo using pomVersion: ${pomVersion}, jansiVersion:${jansiVersion}

JVM_ARGS="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

# JVM_ARGS="${JVM_ARGS} -Djansi.passthrough=true"

CLASSPATH="target/benchmark-jansi-${pomVersion}.jar;target/dependency/jansi-${jansiVersion}.jar"

echo ... java ${JVM_ARGS} -cp "${CLASSPATH}" org.fusesource.jansi.BenchmarkMain 
java ${JVM_ARGS} -cp "${CLASSPATH}" org.fusesource.jansi.BenchmarkMain
