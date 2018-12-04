#!/bin/bash

echo "cf https://docs.confluent.io/current/installation/docker/docs/installation/single-node-client.html"


echo "Creating docker netwok 'confluent'"
docker network create confluent


echo "Starting Zookeeper daemon"
docker run -d \
  --net=confluent \
  --name=zookeeper \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  confluentinc/cp-zookeeper:5.0.1

# docker logs zookeeper


echo "Starting Kafka node daemon"
# docker run -d \
#   --net=confluent \
#   --name=kafka \
#   -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
#   -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 \
#   -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
#   confluentinc/cp-kafka:5.0.1


docker run -d \
  --net=confluent \
  --name=kafka \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092 \
  -p 29092:29092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:5.0.1



echo "Creating Kafka Topic"
docker run \
  --net=confluent \
  --rm confluentinc/cp-kafka:5.0.1 \
  kafka-topics --create --topic foo --partitions 1 --replication-factor 1 \
  --if-not-exists --zookeeper zookeeper:2181

echo "Checking created Kafka Topic"
docker run \
  --net=confluent \
  --rm \
  confluentinc/cp-kafka:5.0.1 \
  kafka-topics --describe --topic foo --zookeeper zookeeper:2181


echo "Running sample Kafka Producer"
docker run \
  --net=confluent \
  --rm \
  confluentinc/cp-kafka:5.0.1 \
  bash -c "seq 42 | kafka-console-producer --request-required-acks 1 \
  --broker-list kafka:9092 --topic foo && echo 'Produced 42 messages.'"  

echo "Running sample Kafka Producer"
docker run \
  --net=confluent \
  --rm \
  confluentinc/cp-kafka:5.0.1 \
  kafka-console-consumer --bootstrap-server kafka:9092 --topic foo --from-beginning --max-messages 42  



echo "Running schema registry"

docker run -d \
  --net=confluent \
  --name=schema-registry \
  -e SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL=zookeeper:2181 \
  -e SCHEMA_REGISTRY_HOST_NAME=schema-registry \
  -e SCHEMA_REGISTRY_LISTENERS=http://0.0.0.0:8081 \
  confluentinc/cp-schema-registry:5.0.1  


# echo "Interactive check bash in Schema registry"  
# docker run -it --net=confluent --rm confluentinc/cp-schema-registry:5.0.1 bash  
# 
# /usr/bin/kafka-avro-console-producer \
#   --broker-list kafka:9092 --topic bar \
#   --property schema.registry.url=http://schema-registry:8081 \
#   --property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'
  
  
