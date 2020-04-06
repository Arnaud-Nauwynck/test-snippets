
Starting a local Docker registry 
cf: https://docs.docker.com/registry/deploying/#run-a-local-registry


export CERTS_DIR=/devtools/certs
export TLS_CERTIFICATE=local.cert.pem
export TLS_KEY=local.key.pem

export REGISTRY_DIR=/devtools/docker/registry
export REGISTRY_CONF=`pwd`/registry/config-local.yml

to generate certificates if not exist..
if [ ! -e ${CERTS_DIR} ]; then 
	mkdir -p ${CERTS_DIR}; 
fi
if [ ! -e ${CERTS_DIR}/${TLS_KEY} ]; then
	openssl req -new -x509 -sha256 -newkey rsa:2048 -nodes -keyout ${CERTS_DIR}/${TLS_KEY} -days 365 -out ${CERTS_DIR}/${TLS_CERTIFICATE}
fi

export DAEMON_MODE=-d

# run local Docker registry in https mode
docker stop registry; docker rm registry; \
docker run ${DAEMON_MODE} \
  --restart=always \
  --name registry \
  -v ${REGISTRY_CONF}:/etc/docker/registry/config.yml \
  -v ${REGISTRY_DIR}:/var/lib/registry \
  -v ${CERTS_DIR}:/mnt/certs \
  -p 5000:5000 \
  registry:2

# you can override..
#  -e REGISTRY_HTTP_ADDR=0.0.0.0:5000 \
#  -e REGISTRY_HTTP_TLS_CERTIFICATE=/mnt/certs/${TLS_CERTIFICATE} \
#  -e REGISTRY_HTTP_TLS_KEY=/mnt/certs/${TLS_KEY} \

# to check.. 
docker logs registry  

> logs for docker registry start:
time="2018-10-06T14:16:55Z" level=warning msg="No HTTP secret provided - generated random secret. This may cause problems with uploads if multiple registries are behind a load-balancer. To provide a shared secret, fill in http.secret in the configuration file or set the REGISTRY_HTTP_SECRET environment variable." go.version=go1.7.6 instance.id=5f242c9a-3225-4d2d-bf8e-856b8706cc17 version=v2.6.2 
time="2018-10-06T14:16:55Z" level=info msg="redis not configured" go.version=go1.7.6 instance.id=5f242c9a-3225-4d2d-bf8e-856b8706cc17 version=v2.6.2 
time="2018-10-06T14:16:55Z" level=info msg="Starting upload purge in 42m0s" go.version=go1.7.6 instance.id=5f242c9a-3225-4d2d-bf8e-856b8706cc17 version=v2.6.2 
time="2018-10-06T14:16:55Z" level=info msg="using inmemory blob descriptor cache" go.version=go1.7.6 instance.id=5f242c9a-3225-4d2d-bf8e-856b8706cc17 version=v2.6.2 
time="2018-10-06T14:16:55Z" level=info msg="listening on [::]:5000" go.version=go1.7.6 instance.id=5f242c9a-3225-4d2d-bf8e-856b8706cc17 version=v2.6.2 



  
mvn package

> logs:
[INFO] --- jib-maven-plugin:0.9.11:build (docker-jib) @ test-docker-jib-hello-app ---
[WARNING] Base image 'gcr.io/distroless/java' does not use a specific image digest - build may not be reproducible
[INFO] 
[INFO] Containerizing application to localhost:5000/test-docker-jib-hello-app, localhost:5000/test-docker-jib-hello-app:version-0.0.1-SNAPSHOT...
[INFO] 
[INFO] Retrieving registry credentials for localhost:5000...
[INFO] Getting base image gcr.io/distroless/java...
[INFO] Building classes layer...
[INFO] Finalizing...
[INFO] 
[INFO] Container entrypoint set to [java, -Xmx64m, -cp, /app/resources:/app/classes:/app/libs/*, fr.an.tests.dockerjib.App]
[INFO] 
[INFO] Built and pushed image as localhost:5000/test-docker-jib-hello-app, localhost:5000/test-docker-jib-hello-app:version-0.0.1-SNAPSHOT
[INFO] 


result in local docker registry file:
find ${REGISTRY_DIR}

/v2
/v2/repositories
/v2/repositories/test-docker-jib-hello-app
/v2/repositories/test-docker-jib-hello-app/_layers/
	..
/v2/repositories/test-docker-jib-hello-app/_uploads/
	..
/v2/repositories/test-docker-jib-hello-app/_manifests/
	..
/v2/blobs
/v2/blobs/sha256/
	..

# Rebuilding an image after applicative code change => extremely fast!



# Pulling image locally from registry
$ docker pull localhost:5000/test-docker-jib-hello-app
Using default tag: latest
latest: Pulling from test-docker-jib-hello-app
8f125ded1b48: Pull complete 
ba7c544469e5: Pull complete 
43b4357066b0: Pull complete 
26758ad104ca: Pull complete 
Digest: sha256:d647ee79c0198d536bcec8e2195b786118abfa53a8af6f4024f8f0479cf1b0ca
Status: Downloaded newer image for localhost:5000/test-docker-jib-hello-app:latest


# Running built image locally
$ docker run localhost:5000/test-docker-jib-hello-app:latest
Hello world!
arg[0]: Arg1


# modify source code, rebuild/publish image to registry:
...modify for "Hello world (v2)!"
mvn package

#re-run (without re-pulling!) => old code run!
Hello world!
arg[0]: Arg1

# re-pull
$ docker pull localhost:5000/test-docker-jib-hello-app
Using default tag: latest
latest: Pulling from test-docker-jib-hello-app
8f125ded1b48: Already exists 
ba7c544469e5: Already exists 
43b4357066b0: Already exists 
a059ab82ee85: Pull complete 
Digest: sha256:96b573fb0cab9a4bfb34e0af77bc063b265bb894ba6e24d4e9dcbffed5aeab19
Status: Downloaded newer image for localhost:5000/test-docker-jib-hello-app:latest

#re-run => new code run!
$ docker run localhost:5000/test-docker-jib-hello-app:latest
Hello world (v2)!
arg[0]: Arg1


# to stop Registry
docker stop registry; docker rm registry



# Advanced Details ...


# inspecting Docker layers...

docker image inspect localhost:5000/test-docker-jib-hello-app:latest
[
    {
        "Id": "sha256:7e8fc11e9aaf4e8d322945ad77fd920699bf098b5c947b7f450ca052a579f076",
        "RepoTags": [
            "localhost:5000/test-docker-jib-hello-app:latest"
        ],
        "RepoDigests": [
            "localhost:5000/test-docker-jib-hello-app@sha256:96b573fb0cab9a4bfb34e0af77bc063b265bb894ba6e24d4e9dcbffed5aeab19"
        ],
        "Parent": "",
        "Comment": "",
        "Created": "1970-01-01T00:00:00Z",
        "Container": "",
        "ContainerConfig": {
            "Hostname": "",
            "Domainname": "",
            "User": "",
            "AttachStdin": false,
            "AttachStdout": false,
            "AttachStderr": false,
            "Tty": false,
            "OpenStdin": false,
            "StdinOnce": false,
            "Env": null,
            "Cmd": null,
            "Image": "",
            "Volumes": null,
            "WorkingDir": "",
            "Entrypoint": null,
            "OnBuild": null,
            "Labels": null
        },
        "DockerVersion": "",
        "Author": "",
        "Config": {
            "Hostname": "",
            "Domainname": "",
            "User": "",
            "AttachStdin": false,
            "AttachStdout": false,
            "AttachStderr": false,
            "Tty": false,
            "OpenStdin": false,
            "StdinOnce": false,
            "Env": [
                "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin",
                "SSL_CERT_FILE=/etc/ssl/certs/ca-certificates.crt",
                "env-var1=value1"
            ],
            "Cmd": [
                "Arg1"
            ],
            "Image": "",
            "Volumes": null,
            "WorkingDir": "",
            "Entrypoint": [
                "java",
                "-Xmx64m",
                "-cp",
                "/app/resources:/app/classes:/app/libs/*",
                "fr.an.tests.dockerjib.App"
            ],
            "OnBuild": null,
            "Labels": {}
        },
        "Architecture": "amd64",
        "Os": "linux",
        "Size": 118289771,
        "VirtualSize": 118289771,
        "GraphDriver": {
            "Data": {
                "LowerDir": "/var/lib/docker/overlay2/2aceb2851e3dc10d61223553fb016c8dcfb174eac4e31b4efe39509b8441deb9/diff:/var/lib/docker/overlay2/dae054924c5f0a3f744d5171fb801513798626c4a84de9d06819e82f935a7f65/diff:/var/lib/docker/overlay2/171826a49d7cc8a4b7ac9ec29271f950bb292f7ab35a2e45c9cac7b1a1665f85/diff",
                "MergedDir": "/var/lib/docker/overlay2/7a4d91a107183ba9a570c1964e226a29ba57fbc47e768b7dbca357930056d732/merged",
                "UpperDir": "/var/lib/docker/overlay2/7a4d91a107183ba9a570c1964e226a29ba57fbc47e768b7dbca357930056d732/diff",
                "WorkDir": "/var/lib/docker/overlay2/7a4d91a107183ba9a570c1964e226a29ba57fbc47e768b7dbca357930056d732/work"
            },
            "Name": "overlay2"
        },
        "RootFS": {
            "Type": "layers",
            "Layers": [
                "sha256:c09300da45505100cbbad5ae5cd8f80389ed86a162e60f6fa211828ff3f80268",
                "sha256:6189abe095d53c1c9f2bfc8f50128ee876b9a5d10f9eda1564e5f5357d6ffe61",
                "sha256:60a2d5e902b42d2ecece89489c92e17b417b93ec672c347a115f086b88e2e403",
                "sha256:6ce8f95088e0b672c82d1d49bd64c311196d2dc7de4e601fb2b4fb490083f0b9"
            ]
        },
        "Metadata": {
            "LastTagTime": "0001-01-01T00:00:00Z"
        }
    }
]


docker image inspect localhost:5000/test-docker-jib-hello-app:latest | jq .[0].RootFS.Layers
"sha256:c09300da45505100cbbad5ae5cd8f80389ed86a162e60f6fa211828ff3f80268",
"sha256:6189abe095d53c1c9f2bfc8f50128ee876b9a5d10f9eda1564e5f5357d6ffe61",
"sha256:60a2d5e902b42d2ecece89489c92e17b417b93ec672c347a115f086b88e2e403",
"sha256:6ce8f95088e0b672c82d1d49bd64c311196d2dc7de4e601fb2b4fb490083f0b9"

docker image save localhost:5000/test-docker-jib-hello-app:latest -o img1.tar

$ mkdir img1; cd img1; tar xf ../img1.tar 
1000181ae92c3f4c0f360b711218eb85bc70f4a96fcb947e3f05b676d85a7837/
1000181ae92c3f4c0f360b711218eb85bc70f4a96fcb947e3f05b676d85a7837/VERSION
1000181ae92c3f4c0f360b711218eb85bc70f4a96fcb947e3f05b676d85a7837/json
1000181ae92c3f4c0f360b711218eb85bc70f4a96fcb947e3f05b676d85a7837/layer.tar
7e8fc11e9aaf4e8d322945ad77fd920699bf098b5c947b7f450ca052a579f076.json
b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/
b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/VERSION
b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/json
b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/layer.tar
c231622c1414c910f3d25f6460d674f84f732502b808e98a233766b21851136b/
c231622c1414c910f3d25f6460d674f84f732502b808e98a233766b21851136b/VERSION
c231622c1414c910f3d25f6460d674f84f732502b808e98a233766b21851136b/json
c231622c1414c910f3d25f6460d674f84f732502b808e98a233766b21851136b/layer.tar
ff1da63c08122d9ff844cf2e4f439bdec80d354bbc34cff0db96c37a51cd1c8c/
ff1da63c08122d9ff844cf2e4f439bdec80d354bbc34cff0db96c37a51cd1c8c/VERSION
ff1da63c08122d9ff844cf2e4f439bdec80d354bbc34cff0db96c37a51cd1c8c/json
ff1da63c08122d9ff844cf2e4f439bdec80d354bbc34cff0db96c37a51cd1c8c/layer.tar
manifest.json
repositories


$ tar tf ./ff1da63c08122d9ff844cf2e4f439bdec80d354bbc34cff0db96c37a51cd1c8c/layer.tar
./etc/
./etc/ssl/
./etc/ssl/certs/
./etc/ssl/certs/java/
./etc/ssl/certs/java/cacerts
./
./var/
./var/lib/
./var/lib/dpkg/
./var/lib/dpkg/status.d/
./var/lib/dpkg/status.d/zlib1g
./lib/
./lib/x86_64-linux-gnu/
./lib/x86_64-linux-gnu/libz.so.1.2.8
./usr/
./usr/share/
./usr/share/doc/
./usr/share/doc/zlib1g/
	....truncated.....
./lib/x86_64-linux-gnu/libz.so.1
./var/lib/dpkg/status.d/openjdk
./etc/java-8-openjdk/
	....truncated.....
./usr/lib/
./usr/lib/debug/
./usr/lib/debug/usr/
./usr/lib/debug/usr/lib/
./usr/lib/debug/usr/lib/jvm/
./usr/lib/jvm/
./usr/lib/jvm/.java-1.8.0-openjdk-amd64.jinfo
./usr/lib/jvm/java-8-openjdk-amd64/
	....truncated.....
./usr/share/binfmts/
./usr/share/doc/openjdk-8-jre-headless/
	....truncated.....
./usr/share/gdb/
./usr/share/gdb/auto-load/
./usr/share/gdb/auto-load/usr/
./usr/share/gdb/auto-load/usr/lib/
./usr/share/gdb/auto-load/usr/lib/jvm/
./usr/share/gdb/auto-load/usr/lib/jvm/java-8-openjdk-amd64/
	....truncated.....
./usr/share/lintian/
./usr/share/lintian/overrides/
./usr/share/lintian/overrides/openjdk-8-jre-headless
./usr/lib/debug/usr/lib/jvm/java-1.8.0-openjdk-amd64
./usr/lib/jvm/java-1.8.0-openjdk-amd64
./usr/lib/jvm/java-8-openjdk-amd64/
	....truncated.....
tar: Removing leading `/' from member names
/usr/
/usr/bin/
/usr/bin/java




$ tar tf c231622c1414c910f3d25f6460d674f84f732502b808e98a233766b21851136b/layer.tar
app/
app/classes/
app/classes/fr/
app/classes/fr/an/
app/classes/fr/an/tests/
app/classes/fr/an/tests/dockerjib/
app/classes/fr/an/tests/dockerjib/App.class


tar tf b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/layer.tar
arnaud@arn:~/test-snippets.github/test-docker-jib-hello-app/img1$ tar tf b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/layer.tar 
./
./etc/
./etc/passwd
./home/
./etc/group
./etc/ssl/certs/ca-certificates.crt
./usr/share/doc/ca-certificates/copyright
./tmp/
./etc/nsswitch.conf
./etc/os-release
./var/
./var/lib/
./var/lib/dpkg/
./var/lib/dpkg/status.d/
./var/lib/dpkg/status.d/libc6
./etc/ld.so.conf.d/
./etc/ld.so.conf.d/x86_64-linux-gnu.conf
./lib/
./lib/x86_64-linux-gnu/
	....truncated.....
./lib64/
./usr/
./usr/lib/
./usr/lib/x86_64-linux-gnu/
	....truncated.....
./usr/share/
./usr/share/doc/
./usr/share/doc/libc6/
./usr/share/doc/libc6/BUGS
./usr/share/doc/libc6/NEWS.Debian.gz
./usr/share/doc/libc6/NEWS.gz
./usr/share/doc/libc6/README.Debian.gz
./usr/share/doc/libc6/README.hesiod.gz
./usr/share/doc/libc6/changelog.Debian.gz
./usr/share/doc/libc6/changelog.gz
./usr/share/doc/libc6/copyright
./usr/share/lintian/
./usr/share/lintian/overrides/
./usr/share/lintian/overrides/libc6
./lib/x86_64-linux-gnu/
	....truncated.....
./lib64/ld-linux-x86-64.so.2
./var/lib/dpkg/status.d/libssl1
./usr/lib/x86_64-linux-gnu/engines-1.1/
./usr/lib/x86_64-linux-gnu/engines-1.1/capi.so
./usr/lib/x86_64-linux-gnu/engines-1.1/padlock.so
./usr/lib/x86_64-linux-gnu/libcrypto.so.1.1
./usr/lib/x86_64-linux-gnu/libssl.so.1.1
./usr/share/doc/libssl1.1/
	....truncated.....
./var/lib/dpkg/status.d/openssl
./etc/ssl/
./etc/ssl/certs/
./etc/ssl/openssl.cnf
./etc/ssl/private/
./usr/bin/
./usr/bin/c_rehash
./usr/bin/openssl
./usr/lib/ssl/
./usr/lib/ssl/misc/
./usr/lib/ssl/misc/CA.pl
./usr/lib/ssl/misc/tsget
./usr/share/doc/openssl/
./usr/share/doc/openssl/FAQ
./usr/share/doc/openssl/HOWTO/
./usr/share/doc/openssl/HOWTO/certificates.txt.gz
./usr/share/doc/openssl/HOWTO/keys.txt
./usr/share/doc/openssl/HOWTO/proxy_certificates.txt.gz
./usr/share/doc/openssl/NEWS.Debian.gz
./usr/share/doc/openssl/NEWS.gz
./usr/share/doc/openssl/README
./usr/share/doc/openssl/README.Debian
./usr/share/doc/openssl/README.ECC
./usr/share/doc/openssl/README.ENGINE.gz
./usr/share/doc/openssl/README.optimization
./usr/share/doc/openssl/changelog.Debian.gz
./usr/share/doc/openssl/changelog.gz
./usr/share/doc/openssl/copyright
./usr/share/doc/openssl/fingerprints.txt
./usr/share/lintian/overrides/openssl
./usr/share/man/
	....truncated.....
./usr/lib/ssl/certs
./usr/lib/ssl/openssl.cnf
./usr/lib/ssl/private
./usr/share/man/
	....truncated.....
./var/lib/dpkg/status.d/netbase
./etc/network/
./etc/protocols
./etc/rpc
./etc/services
./usr/share/doc/netbase/
./usr/share/doc/netbase/changelog.gz
./usr/share/doc/netbase/copyright
./var/lib/dpkg/status.d/tzdata
./usr/sbin/
./usr/sbin/tzconfig
./usr/share/doc/tzdata/
	....truncated.....
./usr/share/zoneinfo/
	....truncated.....



tar tf 1000181ae92c3f4c0f360b711218eb85bc70f4a96fcb947e3f05b676d85a7837/layer.tar
./
./var/
./var/lib/
./var/lib/dpkg/
./var/lib/dpkg/status.d/
./var/lib/dpkg/status.d/libgcc1
./lib/
./lib/x86_64-linux-gnu/
./lib/x86_64-linux-gnu/libgcc_s.so.1
./usr/
./usr/share/
./usr/share/doc/
./usr/share/lintian/
./usr/share/lintian/overrides/
./usr/share/lintian/overrides/libgcc1
./usr/share/doc/libgcc1
./var/lib/dpkg/status.d/libgomp1
./usr/lib/
./usr/lib/x86_64-linux-gnu/
./usr/lib/x86_64-linux-gnu/libgomp.so.1.0.0
./usr/lib/x86_64-linux-gnu/libgomp.so.1
./usr/share/doc/libgomp1
./var/lib/dpkg/status.d/libstdc
./usr/lib/x86_64-linux-gnu/libstdc++.so.6.0.22
./usr/share/gcc-6/
./usr/share/gcc-6/python/
./usr/share/gcc-6/python/libstdcxx/
./usr/share/gcc-6/python/libstdcxx/__init__.py
./usr/share/gcc-6/python/libstdcxx/v6/
./usr/share/gcc-6/python/libstdcxx/v6/__init__.py
./usr/share/gcc-6/python/libstdcxx/v6/printers.py
./usr/share/gcc-6/python/libstdcxx/v6/xmethods.py
./usr/share/gdb/
./usr/share/gdb/auto-load/
./usr/share/gdb/auto-load/usr/
./usr/share/gdb/auto-load/usr/lib/
./usr/share/gdb/auto-load/usr/lib/x86_64-linux-gnu/
./usr/share/gdb/auto-load/usr/lib/x86_64-linux-gnu/libstdc++.so.6.0.22-gdb.py
./usr/lib/x86_64-linux-gnu/libstdc++.so.6
./usr/share/doc/libstdc++6




$ docker image inspect localhost:5000/test-docker-jib-hello-springboot:latest | jq .[0].RootFS.Layers
[
  "sha256:c09300da45505100cbbad5ae5cd8f80389ed86a162e60f6fa211828ff3f80268",
  "sha256:6189abe095d53c1c9f2bfc8f50128ee876b9a5d10f9eda1564e5f5357d6ffe61",
  "sha256:60a2d5e902b42d2ecece89489c92e17b417b93ec672c347a115f086b88e2e403",
  "sha256:2b008c0c48407d652164474698062d44a8ed62d0445f5636b3e0ec908778b88a",
  "sha256:8286ce59a5c8d62d7444a1e622942eec916e6d9599023a022dc6a2c7abef294c"
]

... 5 layers instead of 4 as in test-docker-jib-hello-app

cat manifest.json | jq .[0].Layers
[
  "b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/layer.tar",
  "1000181ae92c3f4c0f360b711218eb85bc70f4a96fcb947e3f05b676d85a7837/layer.tar",
  "ff1da63c08122d9ff844cf2e4f439bdec80d354bbc34cff0db96c37a51cd1c8c/layer.tar",
  "7f5f9721861e13f90b1294e8659377cfdcf4add42b347af847475c8ed86bc0c9/layer.tar",
  "b216ad344a7f02973210edd752dc9e7df4a31dcd1b7c66d7308dd6a80cb11c49/layer.tar"
]

tar tf   b8e07a381fbd8ca7c064f7a6dd29bb9fdbcd707d71df2cb1378f5a3eda96f8d3/layer.tar
	..Zone, ..
tar tf 1000181ae92c3f4c0f360b711218eb85bc70f4a96fcb947e3f05b676d85a7837/layer.tar
	.. libc
tar tf ff1da63c08122d9ff844cf2e4f439bdec80d354bbc34cff0db96c37a51cd1c8c/layer.tar
	.. jdk
	
$ tar tf 7f5f9721861e13f90b1294e8659377cfdcf4add42b347af847475c8ed86bc0c9/layer.tar
app/
app/libs/
app/libs/javax.annotation-api-1.3.2.jar
app/libs/jul-to-slf4j-1.7.25.jar
app/libs/log4j-api-2.10.0.jar
app/libs/log4j-to-slf4j-2.10.0.jar
app/libs/logback-classic-1.2.3.jar
app/libs/logback-core-1.2.3.jar
app/libs/slf4j-api-1.7.25.jar
app/libs/snakeyaml-1.19.jar
app/libs/spring-aop-5.0.9.RELEASE.jar
app/libs/spring-beans-5.0.9.RELEASE.jar
app/libs/spring-boot-2.0.5.RELEASE.jar
app/libs/spring-boot-autoconfigure-2.0.5.RELEASE.jar
app/libs/spring-boot-starter-2.0.5.RELEASE.jar
app/libs/spring-boot-starter-logging-2.0.5.RELEASE.jar
app/libs/spring-context-5.0.9.RELEASE.jar
app/libs/spring-core-5.0.9.RELEASE.jar
app/libs/spring-expression-5.0.9.RELEASE.jar
app/libs/spring-jcl-5.0.9.RELEASE.jar

Contains all the maven dependencies (mvn dependency:copy) ..


$ tar tf b216ad344a7f02973210edd752dc9e7df4a31dcd1b7c66d7308dd6a80cb11c49/layer.tar
app/
app/classes/
app/classes/fr/
app/classes/fr/an/
app/classes/fr/an/testsdockerjib/
app/classes/fr/an/testsdockerjib/hellospring/
app/classes/fr/an/testsdockerjib/hellospring/App.class
app/classes/fr/an/testsdockerjib/hellospring/AppCommandRunner.class



	
  