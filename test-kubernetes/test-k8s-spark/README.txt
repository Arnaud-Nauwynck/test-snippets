
test-k8s-spark



git clone git@github.com:apache/spark.git
cd spark
dev/make-distribution.sh -Pkubernetes
# or .. mvn package -Pkubernetes
cd dist
docker build -t spark:a_3.1.0_2 -f kubernetes/dockerfiles/spark/Dockerfile .

# or .. ./bin/docker-image-tool.sh build
# or .. ./bin/docker-image-tool.sh -t a_3.1.0 build



$ docker image ls
REPOSITORY                           TAG                 IMAGE ID            CREATED             SIZE
spark                                a_3.1.0             15f2dcf2bee3        2 minutes ago       427MB



./a_spark-submit-pi.sh

./a_spark-submit-test.sh

