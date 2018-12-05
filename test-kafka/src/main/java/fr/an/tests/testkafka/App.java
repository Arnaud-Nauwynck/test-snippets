package fr.an.tests.testkafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class App {
	
	private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Test Kafka Producer - Consumer");
        
        String bootstrapServers = "localhost:29092";
        String topic = "foo";
        SimpleKafkaConsumer consumer = new SimpleKafkaConsumer(bootstrapServers, topic);
        SimpleKafkaProducer producer = new SimpleKafkaProducer(bootstrapServers, topic);
        
        new Thread(() -> consumer.runPoll(5)).start();
        new Thread(() -> producer.runProduces(3)).start();

        try {
            log.info("sleep 4s before exiting");
			Thread.sleep(4_000);
		} catch (InterruptedException e) {
		}

        
        try {
            log.info("producer.shutdown()");
			producer.shutdown();
		} catch (InterruptedException e) {
			log.warn("interrupted within producer.shutdown() .. ignore ex");
		}
        log.info(".. producer stopped");

        try {
            log.info("consumer.shutdown()");
			consumer.shutdown();
		} catch (InterruptedException e) {
			log.warn("interrupted within consumer.shutdown() .. ignore ex");
		}
        log.info(".. consumer stopped");
        
        log.info("exiting");
    }
}
