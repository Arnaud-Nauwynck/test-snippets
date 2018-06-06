
Initial configuration

```
basedir=$(pwd)
cd src/test

export TMP_JENKINS_DIR=$basedir/src/test/jenkins-dir1
export JENKINS_HOME=$basedir/src/test/jenkins-plugins
export JENKINS_PLUGINS=$JENKINS_HOME/plugins

if [ ! -e jenkins.war ]; then
	wget http://mirrors.jenkins.io/war-stable/latest/jenkins.war
	unzip jenkins.war -d $TMP_JENKINS_DIR
fi

java -jar jenkins.war
echo .. go to http://localhost:8080/, follow the installation step
echo .. and install the recommended set of plugins

export JENKINS_FILE_RUNNER_HOME=$HOME/downloadTools/maven/jenkinsfile-runner/app/target/appassembler
export PATH=$PATH:$JENKINS_FILE_RUNNER_HOME/bin
```

executing ...

```
jenkinsfile-runner -w $TMP_JENKINS_DIR -p $JENKINS_PLUGINS -f ./test1
```

=> Result

```
runner/src/test/jenkins-dir1 -p /mnt/a_1tera2/homeData/arnaud/perso/devPerso/my-github/test-snippets.github/test-jenkinsfile-runner/src/test/jenkins-plugins/plugins -f ./Jenkinsfile
Started
Running in Durability level: PERFORMANCE_OPTIMIZED
[Pipeline] node
Running on Jenkins in /tmp/jenkinsTests.tmp/jenkins2596774339140602593test/workspace/job
[Pipeline] {
[Pipeline] stage
[Pipeline] { (Declarative: Checkout SCM)
[Pipeline] checkout
[Pipeline] }
[Pipeline] // stage
[Pipeline] stage
[Pipeline] { (Build)
[Pipeline] echo
Hello world!
[Pipeline] sh
[job] Running shell script
+ ls -la
total 12
drwxrwxr-x 2 arnaud arnaud 4096 juin   6 22:24 .
drwxrwxr-x 4 arnaud arnaud 4096 juin   6 22:24 ..
-rw-rw-r-- 1 arnaud arnaud  179 juin   6 22:24 Jenkinsfile
[Pipeline] }
[Pipeline] // stage
[Pipeline] }
[Pipeline] // node
[Pipeline] End of Pipeline
Finished: SUCCESS
```

