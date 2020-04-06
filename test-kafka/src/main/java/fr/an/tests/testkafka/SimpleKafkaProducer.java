package fr.an.tests.testkafka;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleKafkaProducer {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleKafkaProducer.class);

	private final String boostrapServers;
	private final String topic;

	private Producer<String, String> producer;
	private Callback callback = (recordMetadata, e) -> callback_onCompletion(recordMetadata, e);

	private Thread runningThread;
	
	private final AtomicBoolean shutdown = new AtomicBoolean(false);
	private final CountDownLatch shutdownLatch = new CountDownLatch(1);

	public SimpleKafkaProducer(String boostrapServers, String topic) {
		this.boostrapServers = boostrapServers;
		this.topic = topic;
	}

	public void shutdown() throws InterruptedException {
		// first chance: send signal for stopping producer loop 
		shutdown.set(true);
		if (shutdownLatch.await(500, TimeUnit.MILLISECONDS)) {
			return;
		} else {
			// second chance: interrupt thread
			Thread th = runningThread;
			if (th != null) {
				th.interrupt();
			}
			if (shutdownLatch.await(500, TimeUnit.MILLISECONDS)) {
				return;
			}
			
			// last chance.. kill not multi-thread safe!
			closeQuietly(); // ConcurrentModificationException: KafkaConsumer is not safe for multi-threaded access
			
			shutdownLatch.await();
		}
	}
	
	public void runProduces(int count) {
		if (producer != null) {
			log.info("already started producer..");
			return;
		}
		runningThread = Thread.currentThread();
		
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, boostrapServers);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

		this.producer = new KafkaProducer<>(props);
		log.info("new producer");
		try {
		    List<PartitionInfo> partitions = producer.partitionsFor(topic);
		    log.info("partitionsFor(" + topic + "): " + partitions);

			for (long i = 0; i < count && !shutdown.get(); i++) {
				ProducerRecord<String, String> data = new ProducerRecord<>(topic, "key-" + i, "message-" + i);
				log.info("producer.send(.." + i + ")");
				producer.send(data, callback);
			}
	
		    Map<MetricName, ? extends Metric> metrics = producer.metrics();
		    log.info("producer.metrics(): " + metrics);
		    
		    
		} catch(org.apache.kafka.common.errors.InterruptException ex) {
			log.info("interrupted .. stop producer");
		} catch(RuntimeException ex) {
			if (shutdown.get()) {
				log.info("producer request to shutdown ..ignore ex " + ex.getMessage());
			} else {
				throw ex;
			}
		} finally {
			shutdownLatch.countDown();
			log.info("producer.close()");
			closeQuietly();
		}
	}

	private void closeQuietly() {
		if (producer != null) {
			try { 
				producer.close(100, TimeUnit.MILLISECONDS);
			} catch(org.apache.kafka.common.errors.InterruptException ex) {
				log.info("producer interrupted .. within close() .. ignore");
			} catch(Exception ex) {
				log.warn("Failed producer.close(); .. ignore, no rethrow", ex);
			}
			producer = null;
			runningThread = null;
		}
	}

	private void callback_onCompletion(RecordMetadata recordMetadata, Exception e) {
		String shutdownRequestedText = shutdown.get()? " (requested shutdown) " : "";
		if (e != null) {
			log.error(shutdownRequestedText + "Error while producing message to topic :" + recordMetadata, e);
		} else {
			log.info(String.format(shutdownRequestedText + "sent message to topic:%s partition:%s  offset:%s",
					recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset()));
		}
	}


}
