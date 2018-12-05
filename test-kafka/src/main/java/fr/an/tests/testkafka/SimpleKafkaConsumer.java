package fr.an.tests.testkafka;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKafkaConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleKafkaConsumer.class);

	private final String boostrapServers;
	private final String topic;

	private KafkaConsumer<byte[], byte[]> consumer;
	private Thread runningThread;
	
	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final CountDownLatch shutdownLatch = new CountDownLatch(1);



	public SimpleKafkaConsumer(String boostrapServers, String topic) {
		this.boostrapServers = boostrapServers;
		this.topic = topic;
	}
	
	public void shutdown() throws InterruptedException {
		// first chance: send signal for stopping consumer loop 
		shutdown.set(true);
		if (shutdownLatch.await(500, TimeUnit.MILLISECONDS)) {
			return;
		} else {
			// second chance: interrupt thread
			Thread th = runningThread;
			if (th != null) {
				th.interrupt();
			}

			// it is possible to see this ERROR logged by AbstractCoordinator:
//			org.apache.kafka.common.errors.InterruptException: java.lang.InterruptedException
//			at org.apache.kafka.clients.consumer.internals.AbstractCoordinator.closeHeartbeatThread(AbstractCoordinator.java:380)
//			at org.apache.kafka.clients.consumer.internals.AbstractCoordinator.close(AbstractCoordinator.java:777)
//			at org.apache.kafka.clients.consumer.internals.ConsumerCoordinator.close(ConsumerCoordinator.java:580)
//			at org.apache.kafka.clients.consumer.KafkaConsumer.close(KafkaConsumer.java:2140)
//			at org.apache.kafka.clients.consumer.KafkaConsumer.close(KafkaConsumer.java:2108)
//			at fr.an.tests.testkafka.SimpleKafkaConsumer.closeQuietly(SimpleKafkaConsumer.java:118)
//			at fr.an.tests.testkafka.SimpleKafkaConsumer.runPoll(SimpleKafkaConsumer.java:111)
//			at fr.an.tests.testkafka.App.lambda$0(App.java:21)
//			at java.lang.Thread.run(Thread.java:748)
//		Caused by: java.lang.InterruptedException: null
//			at java.lang.Object.wait(Native Method)
//			at java.lang.Thread.join(Thread.java:1252)
//			at java.lang.Thread.join(Thread.java:1326)
//			at org.apache.kafka.clients.consumer.internals.AbstractCoordinator.closeHeartbeatThread(AbstractCoordinator.java:377)
//			... 8 common frames omitted
//		08:35:36.033 4407 [Thread-0] INFO  f.a.t.testkafka.SimpleKafkaConsumer - consumer interrupted .. within close() .. ignore 

		
			if (shutdownLatch.await(500, TimeUnit.MILLISECONDS)) {
				return;
			}
			
			// last chance.. kill not multi-thread safe!
			closeQuietly(); // ConcurrentModificationException: KafkaConsumer is not safe for multi-threaded access
			
			shutdownLatch.await();
		}
	}

	public void runPoll(int maxPoll) {
		if (consumer != null) {
			log.info("already started consumer..");
			return;
		}
		runningThread = Thread.currentThread();
		
		Properties consumerConfig = new Properties();
		consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
		consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");
		consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringDeserializer");

		consumer = new KafkaConsumer<>(consumerConfig);
		
		try {
			TestConsumerRebalanceListener rebalanceListener = new TestConsumerRebalanceListener();

			log.info("consumer.subscribe(" + topic + ")");
			consumer.subscribe(Collections.singletonList(topic), rebalanceListener);

	
			for(int i = 0; i < maxPoll && !shutdown.get(); i++) {
				log.info("consumer.poll(timeout=1s)");
				ConsumerRecords<byte[], byte[]> records = consumer.poll(Duration.ofMillis(1000));

				for (ConsumerRecord<byte[], byte[]> record : records) {
					log.info(String.format("Received Message topic=%s, partition=%s, offset=%d, key=%s, value=%s\n",
							record.topic(), record.partition(), record.offset(), record.key(), record.value()));
				}
	
				log.info("consumer.commitSync()");
				consumer.commitSync();
			}

		    Map<MetricName, ? extends Metric> metrics = consumer.metrics();
		    log.info("consumer.metrics(): " + metrics);

		} catch(org.apache.kafka.common.errors.InterruptException ex) {
			log.info("interrupted .. stop consumer");
		} catch(RuntimeException ex) {
			if (shutdown.get()) {
				log.info("consumer request to shutdown ..ignore ex " + ex.getMessage());
			} else {
				throw ex;
			}
		} finally {
			shutdownLatch.countDown();
			log.info("consumer.close()");
			closeQuietly();
		}			
	}

	private void closeQuietly() {
		if (consumer != null) {
			try {
				consumer.close(Duration.ofMillis(100));
			} catch(org.apache.kafka.common.errors.InterruptException ex) {
				log.info("consumer interrupted .. within close() .. ignore");
			} catch(Exception ex) {
				log.warn("Failed consumer.close(); .. ignore, no rethrow", ex);
			}
			consumer = null;
			runningThread = null;
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