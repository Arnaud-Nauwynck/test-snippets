#!/bin/bash

PROJECT_VERSION=$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version)
TEST_JAR="./target/test-glowroot-inherits-test-${PROJECT_VERSION}.jar"
PLUGIN_JAR="../test-glowroot-inherits-plugin/target/test-glowroot-inherits-plugin-${PROJECT_VERSION}.jar"

JVM_OPTS=""
JVM_OPTS="${JVM_OPTS} -javaagent:./glowroot/glowroot.jar"
JVM_OPTS="${JVM_OPTS} -Xbootclasspath/a:${PLUGIN_JAR}"

# JVM_OPTS="${JVM_OPTS} -Dglowroot.debug.printClassLoading=true"

echo "# java ${JVM_OPTS} -cp ${TEST_JAR} fr.an.test.glowroot.Main"
java ${JVM_OPTS} -cp ${TEST_JAR} fr.an.test.glowroot.Main

