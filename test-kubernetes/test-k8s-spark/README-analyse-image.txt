
# Building Spark + Docker image

bin/mvn -Pkubernetes -Phive -Phive-thriftserver -Phadoop-3.2 package

./bin/docker-image-tool.sh -t a_spark_3 build


# Docker file:
resource-managers/kubernetes/docker/src/main/dockerfiles/spark/Dockerfile

		ARG java_image_tag=8-jre-slim
		
		FROM openjdk:${java_image_tag}
		
		ARG spark_uid=185
		
		# Before building the docker image, first build and make a Spark distribution following
		# the instructions in http://spark.apache.org/docs/latest/building-spark.html.
		# If this docker file is being used in the context of building your images from a Spark
		# distribution, the docker build command should be invoked from the top level directory
		# of the Spark distribution. E.g.:
		# docker build -t spark:latest -f kubernetes/dockerfiles/spark/Dockerfile .
		
		RUN set -ex && \
		    sed -i 's/http:\/\/deb.\(.*\)/https:\/\/deb.\1/g' /etc/apt/sources.list && \
		    apt-get update && \
		    ln -s /lib /lib64 && \
		    apt install -y bash tini libc6 libpam-modules krb5-user libnss3 procps && \
		    mkdir -p /opt/spark && \
		    mkdir -p /opt/spark/examples && \
		    mkdir -p /opt/spark/work-dir && \
		    touch /opt/spark/RELEASE && \
		    rm /bin/sh && \
		    ln -sv /bin/bash /bin/sh && \
		    echo "auth required pam_wheel.so use_uid" >> /etc/pam.d/su && \
		    chgrp root /etc/passwd && chmod ug+rw /etc/passwd && \
		    rm -rf /var/cache/apt/*
		
		COPY jars /opt/spark/jars
		COPY bin /opt/spark/bin
		COPY sbin /opt/spark/sbin
		COPY kubernetes/dockerfiles/spark/entrypoint.sh /opt/
		COPY kubernetes/dockerfiles/spark/decom.sh /opt/
		COPY examples /opt/spark/examples
		COPY kubernetes/tests /opt/spark/tests
		COPY data /opt/spark/data
		
		ENV SPARK_HOME /opt/spark
		
		WORKDIR /opt/spark/work-dir
		RUN chmod g+w /opt/spark/work-dir
		RUN chmod a+x /opt/decom.sh
		
		ENTRYPOINT [ "/opt/entrypoint.sh" ]
		
		# Specify the User that the actual main process will run as
		USER ${spark_uid}
		



Content of "/opt/entrypoint.sh"

		#!/bin/bash
		#
		# Licensed to the Apache Software Foundation (ASF) under one or more
		# contributor license agreements.  See the NOTICE file distributed with
		# this work for additional information regarding copyright ownership.
		# The ASF licenses this file to You under the Apache License, Version 2.0
		# (the "License"); you may not use this file except in compliance with
		# the License.  You may obtain a copy of the License at
		#
		#    http://www.apache.org/licenses/LICENSE-2.0
		#
		# Unless required by applicable law or agreed to in writing, software
		# distributed under the License is distributed on an "AS IS" BASIS,
		# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		# See the License for the specific language governing permissions and
		# limitations under the License.
		#
		
		# echo commands to the terminal output
		set -ex
		
		# Check whether there is a passwd entry for the container UID
		myuid=$(id -u)
		mygid=$(id -g)
		# turn off -e for getent because it will return error code in anonymous uid case
		set +e
		uidentry=$(getent passwd $myuid)
		set -e
		
		# If there is no passwd entry for the container UID, attempt to create one
		if [ -z "$uidentry" ] ; then
		    if [ -w /etc/passwd ] ; then
			echo "$myuid:x:$myuid:$mygid:${SPARK_USER_NAME:-anonymous uid}:$SPARK_HOME:/bin/false" >> /etc/passwd
		    else
			echo "Container ENTRYPOINT failed to add passwd entry for anonymous UID"
		    fi
		fi
		
		SPARK_CLASSPATH="$SPARK_CLASSPATH:${SPARK_HOME}/jars/*"
		env | grep SPARK_JAVA_OPT_ | sort -t_ -k4 -n | sed 's/[^=]*=\(.*\)/\1/g' > /tmp/java_opts.txt
		readarray -t SPARK_EXECUTOR_JAVA_OPTS < /tmp/java_opts.txt
		
		if [ -n "$SPARK_EXTRA_CLASSPATH" ]; then
		  SPARK_CLASSPATH="$SPARK_CLASSPATH:$SPARK_EXTRA_CLASSPATH"
		fi
		
		if [ "$PYSPARK_MAJOR_PYTHON_VERSION" == "2" ]; then
		    pyv="$(python -V 2>&1)"
		    export PYTHON_VERSION="${pyv:7}"
		    export PYSPARK_PYTHON="python"
		    export PYSPARK_DRIVER_PYTHON="python"
		elif [ "$PYSPARK_MAJOR_PYTHON_VERSION" == "3" ]; then
		    pyv3="$(python3 -V 2>&1)"
		    export PYTHON_VERSION="${pyv3:7}"
		    export PYSPARK_PYTHON="python3"
		    export PYSPARK_DRIVER_PYTHON="python3"
		fi
		
		# If HADOOP_HOME is set and SPARK_DIST_CLASSPATH is not set, set it here so Hadoop jars are available to the executor.
		# It does not set SPARK_DIST_CLASSPATH if already set, to avoid overriding customizations of this value from elsewhere e.g. Docker/K8s.
		if [ -n "${HADOOP_HOME}"  ] && [ -z "${SPARK_DIST_CLASSPATH}"  ]; then
		  export SPARK_DIST_CLASSPATH="$($HADOOP_HOME/bin/hadoop classpath)"
		fi
		
		if ! [ -z ${HADOOP_CONF_DIR+x} ]; then
		  SPARK_CLASSPATH="$HADOOP_CONF_DIR:$SPARK_CLASSPATH";
		fi
		
		case "$1" in
		  driver)
		    shift 1
		    CMD=(
		      "$SPARK_HOME/bin/spark-submit"
		      --conf "spark.driver.bindAddress=$SPARK_DRIVER_BIND_ADDRESS"
		      --deploy-mode client
		      "$@"
		    )
		    ;;
		  executor)
		    shift 1
		    CMD=(
		      ${JAVA_HOME}/bin/java
		      "${SPARK_EXECUTOR_JAVA_OPTS[@]}"
		      -Xms$SPARK_EXECUTOR_MEMORY
		      -Xmx$SPARK_EXECUTOR_MEMORY
		      -cp "$SPARK_CLASSPATH:$SPARK_DIST_CLASSPATH"
		      org.apache.spark.executor.CoarseGrainedExecutorBackend
		      --driver-url $SPARK_DRIVER_URL
		      --executor-id $SPARK_EXECUTOR_ID
		      --cores $SPARK_EXECUTOR_CORES
		      --app-id $SPARK_APPLICATION_ID
		      --hostname $SPARK_EXECUTOR_POD_IP
		    )
		    ;;
		
		  *)
		    echo "Non-spark-on-k8s command provided, proceeding in pass-through mode..."
		    CMD=("$@")
		    ;;
		esac
		
		# Execute the container CMD under tini for better hygiene
		exec /usr/bin/tini -s -- "${CMD[@]}"




# Saving to a registry ..

docker tag spark:a_spark_3 localhost:5000/spark:a_spark_3
docker push localhost:5000/spark:a_spark_3



# Exploring docker layers content

dive localhost:5000/spark:a_spark_3





# Analysing docker image...

docker image inspect spark:a_spark_3

..
           "Layers": [
                "sha256:13cb14c2acd34e45446a50af25cb05095a17624678dbafbcc9e26086547c1d74",
                "sha256:94cf29cec5e1e69ddeef0d0e43648b0185e0931920a0b37eda718a5ebe25fa46",
                "sha256:91383663cf66a5d0a58bfc85d22af2f29ff1bbc91b44f5a7f11e5df4cc2c0836",
                "sha256:e892760baedf7a236e99614aeeb9e555c1110a0ea572df87c0ce5b3fd60ea3f8",
                "sha256:675e19f64572af07d91f4209f8b98d46d79705ebe078d1d0505bb2e3e952dbc4",
                "sha256:482d1825da53ee4746f0f037e487bb20c3165cca36d20d033c0a27e5233490bd",
                "sha256:86543615113ca197545a87055e98e07a9738a63e5378d80a23c6063af3598401",
                "sha256:e7d82613f3bc82e49572f3b0d22d75ed87313d20e266fd2ed06aadcbc3c4fa12",
                "sha256:cdaba157dafc5a26109bf2914148aae81bc0a387b38a2b601d3fbada2ee71fc1",
                "sha256:7090adc253ffe08a079029eb900fc934adfb2af196e27986ade1dd470adf35a0",
                "sha256:494564db540dfdb9f511b4ad8833a1014823bf7eb0c4a9faf6365bde0666577d",
                "sha256:9c985c8822bc1e52c8fbb048818b6636cef7804e3a47c8f845e4f04886c6be69",
                "sha256:c7b9bed857130dff075ef16a2d1d339878c8a24aaf3cd64605e658d7a486462c",
                "sha256:0fb2860b1e81e2302c1c1528592adb21e4ff4322649af6b69a35f4d69d2b99f3",
                "sha256:7090adc253ffe08a079029eb900fc934adfb2af196e27986ade1dd470adf35a0"




# Saving docker image to local tar file

docker image save spark:a_spark_3 -o spark_3.tar

ls -lh spark_3.tar 
-rw------- 1 arnaud arnaud 465M juin  21 11:50 spark_3.tar

tar tf spark_3.tar
.......


	$ find
	.
	./manifest.json
	./e9bef33e5f33c9a2b056e21bb0b44f01158e3432a0b304a8453532b22b76532b
	./e9bef33e5f33c9a2b056e21bb0b44f01158e3432a0b304a8453532b22b76532b/layer.tar
	./e9bef33e5f33c9a2b056e21bb0b44f01158e3432a0b304a8453532b22b76532b/VERSION
	./e9bef33e5f33c9a2b056e21bb0b44f01158e3432a0b304a8453532b22b76532b/json
	./3617c07338188786abd0433708fafe01275aca83c6afa32463e6eeebd4e51b39
	./3617c07338188786abd0433708fafe01275aca83c6afa32463e6eeebd4e51b39/layer.tar
	./3617c07338188786abd0433708fafe01275aca83c6afa32463e6eeebd4e51b39/VERSION
	./3617c07338188786abd0433708fafe01275aca83c6afa32463e6eeebd4e51b39/json
	./0665cda0f118c98198f73c5f11243314241ace859aa167cb46696e17d2fce9c6
	./0665cda0f118c98198f73c5f11243314241ace859aa167cb46696e17d2fce9c6/layer.tar
	./0665cda0f118c98198f73c5f11243314241ace859aa167cb46696e17d2fce9c6/VERSION
	./0665cda0f118c98198f73c5f11243314241ace859aa167cb46696e17d2fce9c6/json
	./17b7941d8f83a2db157e78cfabccbef4b750cc472f1f14e697b739a195b2fa16
	./17b7941d8f83a2db157e78cfabccbef4b750cc472f1f14e697b739a195b2fa16/layer.tar
	./17b7941d8f83a2db157e78cfabccbef4b750cc472f1f14e697b739a195b2fa16/VERSION
	./17b7941d8f83a2db157e78cfabccbef4b750cc472f1f14e697b739a195b2fa16/json
	./8c226e35d9f527d81aa2232fa461e1d3e179834449de423871d231e3983ce00a
	./8c226e35d9f527d81aa2232fa461e1d3e179834449de423871d231e3983ce00a/layer.tar
	./8c226e35d9f527d81aa2232fa461e1d3e179834449de423871d231e3983ce00a/VERSION
	./8c226e35d9f527d81aa2232fa461e1d3e179834449de423871d231e3983ce00a/json
	./054c836c5af71fe290b7ccea77dc8f2d6e12945fb046e771fbfe42e47755c212
	./054c836c5af71fe290b7ccea77dc8f2d6e12945fb046e771fbfe42e47755c212/layer.tar
	./054c836c5af71fe290b7ccea77dc8f2d6e12945fb046e771fbfe42e47755c212/VERSION
	./054c836c5af71fe290b7ccea77dc8f2d6e12945fb046e771fbfe42e47755c212/json
	./1d24e0d7a0da17706e63c005a892088a96ee6bd62e4d3f2c8f6f177b58a4e30d
	./1d24e0d7a0da17706e63c005a892088a96ee6bd62e4d3f2c8f6f177b58a4e30d/layer.tar
	./1d24e0d7a0da17706e63c005a892088a96ee6bd62e4d3f2c8f6f177b58a4e30d/VERSION
	./1d24e0d7a0da17706e63c005a892088a96ee6bd62e4d3f2c8f6f177b58a4e30d/json
	./6776b395160059a5614f29cadf60e29f0dad9dbe26859de2e4608188ce9d0097
	./6776b395160059a5614f29cadf60e29f0dad9dbe26859de2e4608188ce9d0097/layer.tar
	./6776b395160059a5614f29cadf60e29f0dad9dbe26859de2e4608188ce9d0097/VERSION
	./6776b395160059a5614f29cadf60e29f0dad9dbe26859de2e4608188ce9d0097/json
	./34f7d995d12dcfd8dc974edd422977f0f0ca1e0b93583449d0d7d97f5b38a90d
	./34f7d995d12dcfd8dc974edd422977f0f0ca1e0b93583449d0d7d97f5b38a90d/layer.tar
	./34f7d995d12dcfd8dc974edd422977f0f0ca1e0b93583449d0d7d97f5b38a90d/VERSION
	./34f7d995d12dcfd8dc974edd422977f0f0ca1e0b93583449d0d7d97f5b38a90d/json
	./8a64d058840bd31ab91698a98a4c920be3b7e0e18b30bd9c7c7d529e88e5db7e
	./8a64d058840bd31ab91698a98a4c920be3b7e0e18b30bd9c7c7d529e88e5db7e/layer.tar
	./8a64d058840bd31ab91698a98a4c920be3b7e0e18b30bd9c7c7d529e88e5db7e/VERSION
	./8a64d058840bd31ab91698a98a4c920be3b7e0e18b30bd9c7c7d529e88e5db7e/json
	./d9e04a46ab69a067473b730096e88ddf0706ac865c3f06e4742bb37c6fdaf070
	./d9e04a46ab69a067473b730096e88ddf0706ac865c3f06e4742bb37c6fdaf070/layer.tar
	./d9e04a46ab69a067473b730096e88ddf0706ac865c3f06e4742bb37c6fdaf070/VERSION
	./d9e04a46ab69a067473b730096e88ddf0706ac865c3f06e4742bb37c6fdaf070/json
	./82ab632f24cb0530c621551c440e6c67377333ee58fc09f07e4889eec7aca267
	./82ab632f24cb0530c621551c440e6c67377333ee58fc09f07e4889eec7aca267/layer.tar
	./82ab632f24cb0530c621551c440e6c67377333ee58fc09f07e4889eec7aca267/VERSION
	./82ab632f24cb0530c621551c440e6c67377333ee58fc09f07e4889eec7aca267/json
	./f2eb4024677e9fcd12fcd4abcc4a98d1e0032f1c86f9d0aea8d4d11a4a29eca1
	./f2eb4024677e9fcd12fcd4abcc4a98d1e0032f1c86f9d0aea8d4d11a4a29eca1/layer.tar
	./f2eb4024677e9fcd12fcd4abcc4a98d1e0032f1c86f9d0aea8d4d11a4a29eca1/VERSION
	./f2eb4024677e9fcd12fcd4abcc4a98d1e0032f1c86f9d0aea8d4d11a4a29eca1/json
	./f38128b5134b5ea8ad115522c9683e15bfc618365af968867e5f2869d605d1e3.json
	./f53cbe25f12b79ca9392b49c1870607fddfaeff7866095e67f7f4bf3fcfd0f51
	./f53cbe25f12b79ca9392b49c1870607fddfaeff7866095e67f7f4bf3fcfd0f51/layer.tar
	./f53cbe25f12b79ca9392b49c1870607fddfaeff7866095e67f7f4bf3fcfd0f51/VERSION
	./f53cbe25f12b79ca9392b49c1870607fddfaeff7866095e67f7f4bf3fcfd0f51/json
	./1fd05e944b91692f8f69c48ed9a7408f69479f41d401c2810f241c32ca848d43
	./1fd05e944b91692f8f69c48ed9a7408f69479f41d401c2810f241c32ca848d43/layer.tar
	./1fd05e944b91692f8f69c48ed9a7408f69479f41d401c2810f241c32ca848d43/VERSION
	./1fd05e944b91692f8f69c48ed9a7408f69479f41d401c2810f241c32ca848d43/json
	./repositories




$ jq '.[0].Layers[]' manifest.json 
[
  "82ab632f24cb0530c621551c440e6c67377333ee58fc09f07e4889eec7aca267/layer.tar",
  "1d24e0d7a0da17706e63c005a892088a96ee6bd62e4d3f2c8f6f177b58a4e30d/layer.tar",
  "1fd05e944b91692f8f69c48ed9a7408f69479f41d401c2810f241c32ca848d43/layer.tar",
  "054c836c5af71fe290b7ccea77dc8f2d6e12945fb046e771fbfe42e47755c212/layer.tar",
  "f53cbe25f12b79ca9392b49c1870607fddfaeff7866095e67f7f4bf3fcfd0f51/layer.tar",
  "e9bef33e5f33c9a2b056e21bb0b44f01158e3432a0b304a8453532b22b76532b/layer.tar",
  "3617c07338188786abd0433708fafe01275aca83c6afa32463e6eeebd4e51b39/layer.tar",
  "8c226e35d9f527d81aa2232fa461e1d3e179834449de423871d231e3983ce00a/layer.tar",
  "d9e04a46ab69a067473b730096e88ddf0706ac865c3f06e4742bb37c6fdaf070/layer.tar",
  "0665cda0f118c98198f73c5f11243314241ace859aa167cb46696e17d2fce9c6/layer.tar",
  "17b7941d8f83a2db157e78cfabccbef4b750cc472f1f14e697b739a195b2fa16/layer.tar",
  "6776b395160059a5614f29cadf60e29f0dad9dbe26859de2e4608188ce9d0097/layer.tar",
  "f2eb4024677e9fcd12fcd4abcc4a98d1e0032f1c86f9d0aea8d4d11a4a29eca1/layer.tar",
  "34f7d995d12dcfd8dc974edd422977f0f0ca1e0b93583449d0d7d97f5b38a90d/layer.tar",
  "8a64d058840bd31ab91698a98a4c920be3b7e0e18b30bd9c7c7d529e88e5db7e/layer.tar"
]


$ for i in $(jq -r '.[0].Layers[]' manifest.json); do count=$(tar tf $i | wc -l); size=$(stat --printf="%s" $i); echo -e "$i \t  $count \t  $size"; done 
	82ab632f24cb0530c621551c440e6c67377333ee58fc09f07e4889eec7aca267/layer.tar 	  4671 	  72485376
	1d24e0d7a0da17706e63c005a892088a96ee6bd62e4d3f2c8f6f177b58a4e30d/layer.tar 	  506 	  9078784
	1fd05e944b91692f8f69c48ed9a7408f69479f41d401c2810f241c32ca848d43/layer.tar 	  4 	  3584
	054c836c5af71fe290b7ccea77dc8f2d6e12945fb046e771fbfe42e47755c212/layer.tar 	  215 	  106157056
	f53cbe25f12b79ca9392b49c1870607fddfaeff7866095e67f7f4bf3fcfd0f51/layer.tar 	  517 	  79333888
	e9bef33e5f33c9a2b056e21bb0b44f01158e3432a0b304a8453532b22b76532b/layer.tar 	  251 	  215613952
	3617c07338188786abd0433708fafe01275aca83c6afa32463e6eeebd4e51b39/layer.tar 	  31 	  80384
	8c226e35d9f527d81aa2232fa461e1d3e179834449de423871d231e3983ce00a/layer.tar 	  25 	  58368
	d9e04a46ab69a067473b730096e88ddf0706ac865c3f06e4742bb37c6fdaf070/layer.tar 	  2 	  5632
	0665cda0f118c98198f73c5f11243314241ace859aa167cb46696e17d2fce9c6/layer.tar 	  2 	  3584
	17b7941d8f83a2db157e78cfabccbef4b750cc472f1f14e697b739a195b2fa16/layer.tar 	  569 	  3520000
	6776b395160059a5614f29cadf60e29f0dad9dbe26859de2e4608188ce9d0097/layer.tar 	  8 	  12288
	f2eb4024677e9fcd12fcd4abcc4a98d1e0032f1c86f9d0aea8d4d11a4a29eca1/layer.tar 	  63 	  1016320
	34f7d995d12dcfd8dc974edd422977f0f0ca1e0b93583449d0d7d97f5b38a90d/layer.tar 	  3 	  2560
	8a64d058840bd31ab91698a98a4c920be3b7e0e18b30bd9c7c7d529e88e5db7e/layer.tar 	  2 	  77


$ tar tf e9bef33e5f33c9a2b056e21bb0b44f01158e3432a0b304a8453532b22b76532b/layer.tar
	opt/
	opt/spark/
	opt/spark/jars/
	opt/spark/jars/.wh..wh..opq
	opt/spark/jars/HikariCP-2.5.1.jar
	opt/spark/jars/JLargeArrays-1.5.jar
	opt/spark/jars/JTransforms-3.1.jar
	opt/spark/jars/RoaringBitmap-0.7.45.jar
	opt/spark/jars/ST4-4.0.4.jar
	opt/spark/jars/accessors-smart-1.2.jar
	opt/spark/jars/activation-1.1.1.jar
	opt/spark/jars/aircompressor-0.10.jar
	opt/spark/jars/algebra_2.12-2.0.0-M2.jar
	opt/spark/jars/antlr-runtime-3.5.2.jar
	opt/spark/jars/antlr4-runtime-4.7.1.jar
	opt/spark/jars/aopalliance-repackaged-2.6.1.jar
	opt/spark/jars/arpack_combined_all-0.1.jar
	opt/spark/jars/arrow-format-0.15.1.jar
	opt/spark/jars/arrow-memory-0.15.1.jar
	opt/spark/jars/arrow-vector-0.15.1.jar
	opt/spark/jars/audience-annotations-0.5.0.jar
	opt/spark/jars/automaton-1.11-8.jar
	opt/spark/jars/avro-1.8.2.jar
	opt/spark/jars/avro-ipc-1.8.2.jar
	opt/spark/jars/avro-mapred-1.8.2-hadoop2.jar
	opt/spark/jars/bonecp-0.8.0.RELEASE.jar
	opt/spark/jars/breeze-macros_2.12-1.0.jar
	opt/spark/jars/breeze_2.12-1.0.jar
	opt/spark/jars/cats-kernel_2.12-2.0.0-M4.jar
	opt/spark/jars/chill-java-0.9.5.jar
	opt/spark/jars/chill_2.12-0.9.5.jar
	opt/spark/jars/commons-beanutils-1.9.4.jar
	opt/spark/jars/commons-cli-1.2.jar
	opt/spark/jars/commons-codec-1.10.jar
	opt/spark/jars/commons-collections-3.2.2.jar
	opt/spark/jars/commons-compiler-3.1.2.jar
	opt/spark/jars/commons-compress-1.8.1.jar
	opt/spark/jars/commons-configuration2-2.1.1.jar
	opt/spark/jars/commons-crypto-1.0.0.jar
	opt/spark/jars/commons-dbcp-1.4.jar
	opt/spark/jars/commons-httpclient-3.1.jar
	opt/spark/jars/commons-io-2.5.jar
	opt/spark/jars/commons-lang-2.6.jar
	opt/spark/jars/commons-lang3-3.9.jar
	opt/spark/jars/commons-logging-1.1.3.jar
	opt/spark/jars/commons-math3-3.4.1.jar
	opt/spark/jars/commons-net-3.1.jar
	opt/spark/jars/commons-pool-1.5.4.jar
	opt/spark/jars/commons-text-1.6.jar
	opt/spark/jars/compress-lzf-1.0.3.jar
	opt/spark/jars/core-1.1.2.jar
	opt/spark/jars/curator-client-2.13.0.jar
	opt/spark/jars/curator-framework-2.13.0.jar
	opt/spark/jars/curator-recipes-2.13.0.jar
	opt/spark/jars/datanucleus-api-jdo-4.2.4.jar
	opt/spark/jars/datanucleus-core-4.1.17.jar
	opt/spark/jars/datanucleus-rdbms-4.1.19.jar
	opt/spark/jars/derby-10.12.1.1.jar
	opt/spark/jars/dnsjava-2.1.7.jar
	opt/spark/jars/dropwizard-metrics-hadoop-metrics2-reporter-0.1.2.jar
	opt/spark/jars/flatbuffers-java-1.9.0.jar
	opt/spark/jars/generex-1.0.2.jar
	opt/spark/jars/gson-2.2.4.jar
	opt/spark/jars/guava-14.0.1.jar
	opt/spark/jars/hadoop-annotations-3.2.0.jar
	opt/spark/jars/hadoop-auth-3.2.0.jar
	opt/spark/jars/hadoop-client-3.2.0.jar
	opt/spark/jars/hadoop-common-3.2.0.jar
	opt/spark/jars/hadoop-hdfs-client-3.2.0.jar
	opt/spark/jars/hadoop-mapreduce-client-common-3.2.0.jar
	opt/spark/jars/hadoop-mapreduce-client-core-3.2.0.jar
	opt/spark/jars/hadoop-mapreduce-client-jobclient-3.2.0.jar
	opt/spark/jars/hadoop-yarn-api-3.2.0.jar
	opt/spark/jars/hadoop-yarn-client-3.2.0.jar
	opt/spark/jars/hadoop-yarn-common-3.2.0.jar
	opt/spark/jars/hive-beeline-2.3.7.jar
	opt/spark/jars/hive-cli-2.3.7.jar
	opt/spark/jars/hive-common-2.3.7.jar
	opt/spark/jars/hive-exec-2.3.7-core.jar
	opt/spark/jars/hive-jdbc-2.3.7.jar
	opt/spark/jars/hive-llap-common-2.3.7.jar
	opt/spark/jars/hive-metastore-2.3.7.jar
	opt/spark/jars/hive-serde-2.3.7.jar
	opt/spark/jars/hive-shims-0.23-2.3.7.jar
	opt/spark/jars/hive-shims-2.3.7.jar
	opt/spark/jars/hive-shims-common-2.3.7.jar
	opt/spark/jars/hive-shims-scheduler-2.3.7.jar
	opt/spark/jars/hive-storage-api-2.7.1.jar
	opt/spark/jars/hive-vector-code-gen-2.3.7.jar
	opt/spark/jars/hk2-api-2.6.1.jar
	opt/spark/jars/hk2-locator-2.6.1.jar
	opt/spark/jars/hk2-utils-2.6.1.jar
	opt/spark/jars/htrace-core4-4.1.0-incubating.jar
	opt/spark/jars/httpclient-4.5.6.jar
	opt/spark/jars/httpcore-4.4.12.jar
	opt/spark/jars/istack-commons-runtime-3.0.8.jar
	opt/spark/jars/ivy-2.4.0.jar
	opt/spark/jars/jackson-annotations-2.10.0.jar
	opt/spark/jars/jackson-core-2.10.0.jar
	opt/spark/jars/jackson-core-asl-1.9.13.jar
	opt/spark/jars/jackson-databind-2.10.0.jar
	opt/spark/jars/jackson-dataformat-yaml-2.10.0.jar
	opt/spark/jars/jackson-datatype-jsr310-2.10.3.jar
	opt/spark/jars/jackson-jaxrs-base-2.9.5.jar
	opt/spark/jars/jackson-jaxrs-json-provider-2.9.5.jar
	opt/spark/jars/jackson-mapper-asl-1.9.13.jar
	opt/spark/jars/jackson-module-jaxb-annotations-2.10.0.jar
	opt/spark/jars/jackson-module-paranamer-2.10.0.jar
	opt/spark/jars/jackson-module-scala_2.12-2.10.0.jar
	opt/spark/jars/jakarta.activation-api-1.2.1.jar
	opt/spark/jars/jakarta.annotation-api-1.3.5.jar
	opt/spark/jars/jakarta.inject-2.6.1.jar
	opt/spark/jars/jakarta.validation-api-2.0.2.jar
	opt/spark/jars/jakarta.ws.rs-api-2.1.6.jar
	opt/spark/jars/jakarta.xml.bind-api-2.3.2.jar
	opt/spark/jars/janino-3.1.2.jar
	opt/spark/jars/javassist-3.25.0-GA.jar
	opt/spark/jars/javax.jdo-3.2.0-m3.jar
	opt/spark/jars/javax.servlet-api-3.1.0.jar
	opt/spark/jars/javolution-5.5.1.jar
	opt/spark/jars/jaxb-api-2.2.11.jar
	opt/spark/jars/jaxb-runtime-2.3.2.jar
	opt/spark/jars/jcip-annotations-1.0-1.jar
	opt/spark/jars/jcl-over-slf4j-1.7.30.jar
	opt/spark/jars/jdo-api-3.0.1.jar
	opt/spark/jars/jersey-client-2.30.jar
	opt/spark/jars/jersey-common-2.30.jar
	opt/spark/jars/jersey-container-servlet-2.30.jar
	opt/spark/jars/jersey-container-servlet-core-2.30.jar
	opt/spark/jars/jersey-hk2-2.30.jar
	opt/spark/jars/jersey-media-jaxb-2.30.jar
	opt/spark/jars/jersey-server-2.30.jar
	opt/spark/jars/jline-2.14.6.jar
	opt/spark/jars/joda-time-2.10.5.jar
	opt/spark/jars/jodd-core-3.5.2.jar
	opt/spark/jars/jpam-1.1.jar
	opt/spark/jars/json-1.8.jar
	opt/spark/jars/json-smart-2.3.jar
	opt/spark/jars/json4s-ast_2.12-3.6.6.jar
	opt/spark/jars/json4s-core_2.12-3.6.6.jar
	opt/spark/jars/json4s-jackson_2.12-3.6.6.jar
	opt/spark/jars/json4s-scalap_2.12-3.6.6.jar
	opt/spark/jars/jsp-api-2.1.jar
	opt/spark/jars/jsr305-3.0.0.jar
	opt/spark/jars/jta-1.1.jar
	opt/spark/jars/jul-to-slf4j-1.7.30.jar
	opt/spark/jars/kerb-admin-1.0.1.jar
	opt/spark/jars/kerb-client-1.0.1.jar
	opt/spark/jars/kerb-common-1.0.1.jar
	opt/spark/jars/kerb-core-1.0.1.jar
	opt/spark/jars/kerb-crypto-1.0.1.jar
	opt/spark/jars/kerb-identity-1.0.1.jar
	opt/spark/jars/kerb-server-1.0.1.jar
	opt/spark/jars/kerb-simplekdc-1.0.1.jar
	opt/spark/jars/kerb-util-1.0.1.jar
	opt/spark/jars/kerby-asn1-1.0.1.jar
	opt/spark/jars/kerby-config-1.0.1.jar
	opt/spark/jars/kerby-pkix-1.0.1.jar
	opt/spark/jars/kerby-util-1.0.1.jar
	opt/spark/jars/kerby-xdr-1.0.1.jar
	opt/spark/jars/kryo-shaded-4.0.2.jar
	opt/spark/jars/kubernetes-client-4.9.2.jar
	opt/spark/jars/kubernetes-model-4.9.2.jar
	opt/spark/jars/kubernetes-model-common-4.9.2.jar
	opt/spark/jars/leveldbjni-all-1.8.jar
	opt/spark/jars/libfb303-0.9.3.jar
	opt/spark/jars/libthrift-0.12.0.jar
	opt/spark/jars/log4j-1.2.17.jar
	opt/spark/jars/logging-interceptor-3.12.6.jar
	opt/spark/jars/lz4-java-1.7.1.jar
	opt/spark/jars/machinist_2.12-0.6.8.jar
	opt/spark/jars/macro-compat_2.12-1.1.1.jar
	opt/spark/jars/metrics-core-4.1.1.jar
	opt/spark/jars/metrics-graphite-4.1.1.jar
	opt/spark/jars/metrics-jmx-4.1.1.jar
	opt/spark/jars/metrics-json-4.1.1.jar
	opt/spark/jars/metrics-jvm-4.1.1.jar
	opt/spark/jars/minlog-1.3.0.jar
	opt/spark/jars/netty-all-4.1.47.Final.jar
	opt/spark/jars/nimbus-jose-jwt-4.41.1.jar
	opt/spark/jars/objenesis-2.5.1.jar
	opt/spark/jars/okhttp-2.7.5.jar
	opt/spark/jars/okhttp-3.12.6.jar
	opt/spark/jars/okio-1.15.0.jar
	opt/spark/jars/opencsv-2.3.jar
	opt/spark/jars/orc-core-1.5.10.jar
	opt/spark/jars/orc-mapreduce-1.5.10.jar
	opt/spark/jars/orc-shims-1.5.10.jar
	opt/spark/jars/oro-2.0.8.jar
	opt/spark/jars/osgi-resource-locator-1.0.3.jar
	opt/spark/jars/paranamer-2.8.jar
	opt/spark/jars/parquet-column-1.10.1.jar
	opt/spark/jars/parquet-common-1.10.1.jar
	opt/spark/jars/parquet-encoding-1.10.1.jar
	opt/spark/jars/parquet-format-2.4.0.jar
	opt/spark/jars/parquet-hadoop-1.10.1.jar
	opt/spark/jars/parquet-jackson-1.10.1.jar
	opt/spark/jars/protobuf-java-2.5.0.jar
	opt/spark/jars/py4j-0.10.9.jar
	opt/spark/jars/pyrolite-4.30.jar
	opt/spark/jars/re2j-1.1.jar
	opt/spark/jars/scala-collection-compat_2.12-2.1.1.jar
	opt/spark/jars/scala-compiler-2.12.10.jar
	opt/spark/jars/scala-library-2.12.10.jar
	opt/spark/jars/scala-parser-combinators_2.12-1.1.2.jar
	opt/spark/jars/scala-reflect-2.12.10.jar
	opt/spark/jars/scala-xml_2.12-1.2.0.jar
	opt/spark/jars/shapeless_2.12-2.3.3.jar
	opt/spark/jars/shims-0.7.45.jar
	opt/spark/jars/slf4j-api-1.7.30.jar
	opt/spark/jars/slf4j-log4j12-1.7.30.jar
	opt/spark/jars/snakeyaml-1.24.jar
	opt/spark/jars/snappy-java-1.1.7.5.jar
	opt/spark/jars/spark-catalyst_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-core_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-graphx_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-hive-thriftserver_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-hive_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-kubernetes_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-kvstore_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-launcher_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-mllib-local_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-mllib_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-network-common_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-network-shuffle_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-repl_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-sketch_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-sql_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-streaming_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-tags_2.12-3.1.0-SNAPSHOT-tests.jar
	opt/spark/jars/spark-tags_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spark-unsafe_2.12-3.1.0-SNAPSHOT.jar
	opt/spark/jars/spire-macros_2.12-0.17.0-M1.jar
	opt/spark/jars/spire-platform_2.12-0.17.0-M1.jar
	opt/spark/jars/spire-util_2.12-0.17.0-M1.jar
	opt/spark/jars/spire_2.12-0.17.0-M1.jar
	opt/spark/jars/stax-api-1.0.1.jar
	opt/spark/jars/stax2-api-3.1.4.jar
	opt/spark/jars/stream-2.9.6.jar
	opt/spark/jars/super-csv-2.2.0.jar
	opt/spark/jars/threeten-extra-1.5.0.jar
	opt/spark/jars/token-provider-1.0.1.jar
	opt/spark/jars/transaction-api-1.1.jar
	opt/spark/jars/univocity-parsers-2.8.3.jar
	opt/spark/jars/velocity-1.5.jar
	opt/spark/jars/woodstox-core-5.0.3.jar
	opt/spark/jars/xbean-asm7-shaded-4.15.jar
	opt/spark/jars/xz-1.5.jar
	opt/spark/jars/zjsonpatch-0.3.0.jar
	opt/spark/jars/zookeeper-3.4.14.jar
	opt/spark/jars/zstd-jni-1.4.5-2.jar
	





