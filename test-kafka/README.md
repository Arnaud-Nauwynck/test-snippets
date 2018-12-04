

Few links ..

https://docs.confluent.io/current/installation/docker/docs/installation/single-node-client.html

https://github.com/garg-geek/kafka/tree/master/kakfa-producer-consumer-example/src/main/java/com/gaurav/kafka

https://docs.hortonworks.com/HDPDocuments/HDP2/HDP-2.6.5/bk_kafka-component-guide/content/ch_kafka-development.html


Test all-in-one using docker script:


```
# check nothing is running..
docker ps -a 

# mr proper... remove all your docker containers!!
# docker rm -f $(docker ps -aq)

./setup-confluent.sh
```

=> Result logs: 
```
Creating docker netwok 'confluent'
Error response from daemon: network with name confluent already exists
Starting Zookeeper daemon
47d8869fe715f247968e7631f50e4fb4d8d44e3b38a097f929cd5efdb7d404d9
Starting Kafka node daemon
75e48433a0245457ff83bb034fc62013451c8a66a7ca59af9dfd799aea625b4e
Creating Kafka Topic
Error while executing topic command : Replication factor: 1 larger than available brokers: 0.
[2018-12-04 22:16:52,223] ERROR org.apache.kafka.common.errors.InvalidReplicationFactorException: Replication factor: 1 larger than available brokers: 0.
 (kafka.admin.TopicCommand$)
Checking created Kafka Topic
Running sample Kafka Producer
>[2018-12-04 22:16:56,351] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 1 : {foo=LEADER_NOT_AVAILABLE} (org.apache.kafka.clients.NetworkClient)
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Produced 42 messages.
Running sample Kafka Producer
1
2
3
..
(truncated)
..
41
42
Processed a total of 42 messages
Running schema registry
35291af763eaf4f4313907a8828f753cae878767be22f06d51d8a4f20ee0d6ee
```


Check daemons are still running
```
$ docker ps -a
CONTAINER ID        IMAGE                                   COMMAND                  CREATED             STATUS              PORTS                          NAMES
35291af763ea        confluentinc/cp-schema-registry:5.0.1   "/etc/confluent/dock…"   3 minutes ago       Up 3 minutes        8081/tcp                       schema-registry
75e48433a024        confluentinc/cp-kafka:5.0.1             "/etc/confluent/dock…"   3 minutes ago       Up 3 minutes        9092/tcp                       kafka
47d8869fe715        confluentinc/cp-zookeeper:5.0.1         "/etc/confluent/dock…"   3 minutes ago       Up 3 minutes        2181/tcp, 2888/tcp, 3888/tcp   zookeeper
```

Try running java main with localhost:9092 => ConnectionFailed ... 
```
23:44:53.590 348  [kafka-producer-network-thread | producer-1] WARN  o.a.kafka.clients.NetworkClient - [Producer clientId=producer-1] Connection to node -1 could not be established. Broker may not be available. 
```

port is not opened on localhost... but on docker network interface!

```
$ docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
feafc1224aaa        bridge              bridge              local
da6e08eb94a9        confluent           bridge              local
5e57de5d3a0c        host                host                local
346502bcf8ee        none                null                local
```

```
$ docker network inspect confluent
[
    {
        "Name": "confluent",
        "Id": "da6e08eb94a9b263ea7d2fafe9efb123a8cf328476b305c4142edefd0550bcc5",
        "Created": "2018-12-04T23:13:32.109397999+01:00",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.18.0.0/16",
                    "Gateway": "172.18.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "35291af763eaf4f4313907a8828f753cae878767be22f06d51d8a4f20ee0d6ee": {
                "Name": "schema-registry",
                "EndpointID": "2e2219d85cbd9b8aae7f23274acb6ee2158343695e41fd0d5c19a193fd934ea6",
                "MacAddress": "02:42:ac:12:00:04",
                "IPv4Address": "172.18.0.4/16",
                "IPv6Address": ""
            },
            "47d8869fe715f247968e7631f50e4fb4d8d44e3b38a097f929cd5efdb7d404d9": {
                "Name": "zookeeper",
                "EndpointID": "2fe2f21ce525ebd20f546fe2af4410a9acbf2f55e2846f84062e565badd6dd96",
                "MacAddress": "02:42:ac:12:00:02",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            },
            "75e48433a0245457ff83bb034fc62013451c8a66a7ca59af9dfd799aea625b4e": {
                "Name": "kafka",
                "EndpointID": "f19ac40b6ef516b0fb0b3369fc355e5ebaed5398c4136a15af2cce1f81ebffce",
                "MacAddress": "02:42:ac:12:00:03",
                "IPv4Address": "172.18.0.3/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```


Checking "advertised_listener" port opened on docker interface.. ?!
```
$ telnet 172.18.0.3 9092
Trying 172.18.0.3...
Connected to 172.18.0.3.
Escape character is '^]'.
^CConnection closed by foreign host.
```


https://stackoverflow.com/questions/51630260/connect-to-kafka-running-in-docker-from-local-machine

added docker run option ...
```
-e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
-p 29092:29092
```

Re-running java code...
```
00:10:05.704 30367 [kafka-producer-network-thread | producer-1] WARN  o.a.kafka.clients.NetworkClient - [Producer clientId=producer-1] 1 partitions have leader brokers without a matching listener, including [foo-0] 
00:10:05.805 30468 [kafka-producer-network-thread | producer-1] WARN  o.a.kafka.clients.NetworkClient - [Producer clientId=producer-1] 1 partitions have leader brokers without a matching listener, including [foo-0] 
00:10:05.908 30571 [kafka-producer-network-thread | producer-1] ERROR f.a.t.testkafka.SimpleKafkaProducer - Error while producing message to topic :foo-0@-1 
org.apache.kafka.common.errors.TimeoutException: Expiring 5 record(s) for foo-0: 30087 ms has passed since batch creation plus linger time
```


Checking??
```
$ docker run   --net=confluent   --rm   confluentinc/cp-kafka:5.0.1   kafka-topics --describe --topic foo --zookeeper zookeeper:2181
Topic:foo	PartitionCount:1	ReplicationFactor:1	Configs:
	Topic: foo	Partition: 0	Leader: 1001	Replicas: 1001	Isr: 1001
```


Trying docker kafka shell.. 
`̀``
$ docker run   --net=confluent  -it  confluentinc/cp-kafka:5.0.1 bash
root@e38025204190:/# 

root@e38025204190:/# kafka-
kafka-acls                        kafka-consumer-perf-test          kafka-preferred-replica-election  kafka-server-stop
kafka-broker-api-versions         kafka-delegation-tokens           kafka-producer-perf-test          kafka-streams-application-reset
kafka-configs                     kafka-delete-records              kafka-reassign-partitions         kafka-topics
kafka-console-consumer            kafka-dump-log                    kafka-replica-verification        kafka-verifiable-consumer
kafka-console-producer            kafka-log-dirs                    kafka-run-class                   kafka-verifiable-producer
kafka-consumer-groups             kafka-mirror-maker                kafka-server-start       

root@e38025204190:/# kafka-topics --list --zookeeper zookeeper:2181
__confluent.support.metrics
__consumer_offsets
_schemas
foo

root@e38025204190:/# kafka-topics --describe --topic foo --zookeeper zookeeper:2181
Topic:foo	PartitionCount:1	ReplicationFactor:1	Configs:
	Topic: foo	Partition: 0	Leader: 1001	Replicas: 1001	Isr: 1001


root@e38025204190:/# kafka-console-consumer --bootstrap-server kafka:9092 --topic foo 
[2018-12-04 23:33:48,175] WARN [Consumer clientId=consumer-1, groupId=console-consumer-60900] 1 partitions have leader brokers without a matching listener, including [foo-0] (org.apache.kafka.clients.NetworkClient)
[2018-12-04 23:33:48,277] WARN [Consumer clientId=consumer-1, groupId=console-consumer-60900] 1 partitions have leader brokers without a matching listener, including [foo-0] (org.apache.kafka.clients.NetworkClient)
`̀``

The wraning is reproduced by using directly docker + kafka-console-consumer ... but what's wrong? 

??

