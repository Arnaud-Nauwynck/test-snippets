package fr.an.tests.testkafka;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKafkaConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleKafkaConsumer.class);

	private final String boostrapServers;
	private final String topic;

	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final CountDownLatch shutdownLatch = new CountDownLatch(1);



	public SimpleKafkaConsumer(String boostrapServers, String topic) {
		this.boostrapServers = boostrapServers;
		this.topic = topic;
	}
	
	public void shutdown() throws InterruptedException {
		shutdown.set(true);
		shutdownLatch.await();
	}

	public void runPoll(int maxPoll) {
		Properties consumerConfig = new Properties();
		consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
		consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");

		try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerConfig)) {
			TestConsumerRebalanceListener rebalanceListener = new TestConsumerRebalanceListener();

			log.info("consumer.subscribe(" + topic + ")");
			consumer.subscribe(Collections.singletonList(topic), rebalanceListener);

	
			for(int i = 0; i < maxPoll && !shutdown.get(); i++) {
				log.info("consumer.poll(timeout=1s)");
				ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(1000));

				for (ConsumerRecord<byte[], byte[]> record : records) {
					log.info(String.format("Received Message topic =%s, partition =%s, offset = %d, key = %s, value = %s\n",
							record.topic(), record.partition(), record.offset(), record.key(), record.value()));
				}
	
				log.info("consumer.commitSync()");
				consumer.commitSync();
			}

			log.info("consumer.close()");
		} finally {
	      shutdownLatch.countDown();
		}			
	}

	private static class TestConsumerRebalanceListener implements ConsumerRebalanceListener {
		
		private static final Logger log = LoggerFactory.getLogger(SimpleKafkaConsumer.TestConsumerRebalanceListener.class);

		@Override
		public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
		    log.info("Called onPartitionsRevoked with partitions:" + partitions);
		}
		
		@Override
		public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
			log.info("Called onPartitionsAssigned with partitions:" + partitions);
		}
	}
}