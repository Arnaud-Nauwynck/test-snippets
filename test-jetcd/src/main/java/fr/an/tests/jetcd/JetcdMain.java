package fr.an.tests.jetcd;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.Watch.Listener;
import io.etcd.jetcd.Watch.Watcher;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;


// @Slf4j
public class JetcdMain {

	private static final Logger log = LoggerFactory.getLogger(JetcdMain.class);
	
	public static void main(String[] args) {
		try {
			JetcdMain app = new JetcdMain();
			app.run();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void run() throws Exception {
		// create client
		Client etcdClient = Client.builder().endpoints("http://localhost:2379").build();
		KV kvClient = etcdClient.getKVClient();
		// Cluster clusterClient = etcdClient.getClusterClient();
		Watch watchClient = etcdClient.getWatchClient();
		
		
		ByteSequence key = ByteSequence.from("test_key".getBytes());
		ByteSequence value = ByteSequence.from("test_value".getBytes());

		// put the key-value
		kvClient.put(key, value).get();

		// get the CompletableFuture
		GetResponse response = kvClient.get(key).get();
		ByteSequence checkValue = response.getKvs().get(0).getValue();
		String checkValueText = new String(checkValue.getBytes());
		System.out.println("check test_key => " + checkValueText);
		
		// delete the key
		kvClient.delete(key).get();
		
		
		CountDownLatch latch = new CountDownLatch(1);
		
		ByteSequence fromKey = ByteSequence.from("test_".getBytes());
		ByteSequence toKey = ByteSequence.from("test_z".getBytes());
		WatchOption watchOptions = WatchOption.newBuilder()
				.withRange(toKey)
				.build();
		
		Watcher watcher = watchClient.watch(fromKey, watchOptions, new Listener() {

			@Override
			public void onNext(WatchResponse response) {
				List<WatchEvent> events = response.getEvents();
				StringBuilder sb = new StringBuilder();
				int eventsLen = events.size();
				sb.append("## watch => " + ((eventsLen != 1)? eventsLen + " events" : "")); 
				for(WatchEvent event : events) {
					sb.append(" " + event.getEventType() 
						+ " key:" + new String(event.getKeyValue().getKey().getBytes()) 
						+ " value:" + new String(event.getKeyValue().getValue().getBytes())
						);
				}
				log.info(sb.toString());
			}

			@Override
			public void onError(Throwable ex) {
				log.error("## watch => onError", ex);
			}

			@Override
			public void onCompleted() {
				log.error("## watch => onCompleted");
			}
			
		});

		
		ByteSequence key1 = ByteSequence.from("test_k1".getBytes());
		ByteSequence value1 = ByteSequence.from("test_value1".getBytes());
		log.info("put (ins) " + new String(key1.getBytes()) + " : " + new String(value1.getBytes()));
		kvClient.put(key1, value1).get();
		sleep(100);

		log.info("put (same value) " + new String(key1.getBytes()) + " : " + new String(value1.getBytes()));
		kvClient.put(key1, value1).get();
		sleep(100);

		log.info("put (update) " + new String(key1.getBytes()) + " : " + new String(value1.getBytes()));
		ByteSequence value2 = ByteSequence.from("test_value2".getBytes());
		kvClient.put(key1, value2).get();
		sleep(100);

		log.info("delete " + new String(key1.getBytes()));
		kvClient.delete(key1).get();
		sleep(100);
		
		watcher.close();
		
		etcdClient.close();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
}
