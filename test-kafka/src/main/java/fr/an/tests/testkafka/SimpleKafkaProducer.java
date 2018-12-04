package fr.an.tests.testkafka;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKafkaProducer {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleKafkaProducer.class);

	private final String boostrapServers;
	private final String topic;

	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final CountDownLatch shutdownLatch = new CountDownLatch(1);

	public SimpleKafkaProducer(String boostrapServers, String topic) {
		this.boostrapServers = boostrapServers;
		this.topic = topic;
	}

	public void shutdown() throws InterruptedException {
		shutdown.set(true);
		shutdownLatch.await();
	}
	
	public void runProduces(int count) {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

		log.info("new producer");
		try (Producer<String, String> producer = new KafkaProducer<>(props)) {
			TestCallback callback = new TestCallback();
	
			for (long i = 0; i < count && !shutdown.get(); i++) {
				ProducerRecord<String, String> data = new ProducerRecord<>(topic, "key-" + i, "message-" + i);
				log.info("producer.send(.." + i + ")");
				producer.send(data, callback);
			}
	
			log.info("producer.close()");
			producer.close();
		}
	}

	private static class TestCallback implements Callback {
		@Override
		public void onCompletion(RecordMetadata recordMetadata, Exception e) {
			if (e != null) {
				log.error("Error while producing message to topic :" + recordMetadata, e);
			} else {
				log.info(String.format("sent message to topic:%s partition:%s  offset:%s",
						recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset()));
			}
		}
	}
}
