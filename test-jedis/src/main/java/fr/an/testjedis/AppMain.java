package fr.an.testjedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * 
 * A simple Publish-Subscribe test for redis
 * 
 * start redis using
 * <PRE>
 * docker run -it -p16379:6379 redis
 * </PRE>
 * 
 * Typical logs:
 * msg0 may not be received...because subscribe Thread may subscribe after msg0 is published
 * 
 * <PRE>
02:49:03.376 [Thread-3] INFO  fr.an.testjedis.AppMain - publish msg0
02:49:03.379 [Thread-4] INFO  fr.an.testjedis.AppMain - onSubscribe redis-chanel1
02:49:03.381 [Thread-4] INFO  fr.an.testjedis.AppMain - onMessage redis-chanel1 : msg0
02:49:03.479 [Thread-3] INFO  fr.an.testjedis.AppMain - publish msg1
02:49:03.480 [Thread-4] INFO  fr.an.testjedis.AppMain - onMessage redis-chanel1 : msg1
02:49:03.580 [Thread-3] INFO  fr.an.testjedis.AppMain - publish msg2
02:49:03.585 [Thread-4] INFO  fr.an.testjedis.AppMain - onMessage redis-chanel1 : msg2
02:49:03.685 [Thread-3] INFO  fr.an.testjedis.AppMain - publish msg3
02:49:03.685 [Thread-4] INFO  fr.an.testjedis.AppMain - onMessage redis-chanel1 : msg3
02:49:03.785 [Thread-3] INFO  fr.an.testjedis.AppMain - publish msg4
02:49:03.786 [Thread-4] INFO  fr.an.testjedis.AppMain - onMessage redis-chanel1 : msg4
02:49:03.886 [Thread-3] INFO  fr.an.testjedis.AppMain - finish subscription (still publish 2 msgs..)
02:49:03.886 [Thread-3] INFO  fr.an.testjedis.AppMain - publish msg5
02:49:03.886 [Thread-4] INFO  fr.an.testjedis.AppMain - onMessage redis-chanel1 : msg5
02:49:03.886 [Thread-4] INFO  fr.an.testjedis.AppMain - onUnsubscribe redis-chanel1
02:49:03.887 [Thread-4] INFO  fr.an.testjedis.AppMain - subscribtion finished
02:49:03.887 [Thread-4] INFO  fr.an.testjedis.AppMain - ... done runSubscribeThread
02:49:03.986 [Thread-3] INFO  fr.an.testjedis.AppMain - publish msg6
02:49:04.087 [Thread-3] INFO  fr.an.testjedis.AppMain - ... done runPublishThread
 * </PRE>
 * 
 */
public class AppMain {
	
	private static final Logger LOG = LoggerFactory.getLogger(AppMain.class);
	
	private JedisPool jedisPool;
	
	private String redisHost = "localhost";
	private int redisPort = 16379; // default 6379 ... using for test: "docker run -it -p16379:6379 redis"  
	
	private String redisChannel = "redis-chanel1";
	
	private JedisPubSub jedisPubSub;
	
	public static void main(String[] args) {
		new AppMain().doMain();		
	}

	private void doMain() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig, redisHost, redisPort, 20000);
        
        new Thread(() -> runPublishThread()).start();
        new Thread(() -> runSubscribeThread()).start();
	}

	private void runPublishThread() {
		try (Jedis conn = jedisPool.getResource()) {
			for(int i = 0; i < 5; i++) {
				String msg = "msg" + i;
				LOG.info("publish " + msg);
				conn.publish(redisChannel, msg);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

			LOG.info("finish subscription (still publish 2 msgs..)");
			jedisPubSub.unsubscribe();

			for(int i = 5; i < 7; i++) {
				String msg = "msg" + i;
				LOG.info("publish " + msg);
				conn.publish(redisChannel, msg);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}

		LOG.info("... done runPublishThread");
	}

	private void runSubscribeThread() {
		try (Jedis conn = jedisPool.getResource()) {
			jedisPubSub = new JedisPubSub() {

				@Override
				public void onMessage(String channel, String message) {
					LOG.info("onMessage " + channel + " : " + message);
				}

				@Override
				public void onPMessage(String pattern, String channel, String message) {
					LOG.info("onPMessage pattern:" + pattern + " " + channel + " : " + message);
				}

				@Override
				public void onSubscribe(String channel, int subscribedChannels) {
					LOG.info("onSubscribe " + channel);
				}

				@Override
				public void onUnsubscribe(String channel, int subscribedChannels) {
					LOG.info("onUnsubscribe " + channel);
				}

				@Override
				public void onPUnsubscribe(String pattern, int subscribedChannels) {
					LOG.info("onPUnsubscribe pattern:" + pattern);
				}

				@Override
				public void onPSubscribe(String pattern, int subscribedChannels) {
					LOG.info("onPSubscribe pattern:" + pattern);
				}

				@Override
				public void onPong(String pattern) {
					LOG.info("onPong " + pattern);
				}

				@Override
				public void ping() {
					LOG.info("onPing");
				}
				
			};
			conn.subscribe(jedisPubSub, redisChannel); //<= does not return until subscription finished!
			LOG.info("subscribtion finished");
						
			LOG.info("... done runSubscribeThread");
		}
	}

}
