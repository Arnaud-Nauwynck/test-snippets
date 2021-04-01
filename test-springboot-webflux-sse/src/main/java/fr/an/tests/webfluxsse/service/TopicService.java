package fr.an.tests.webfluxsse.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import fr.an.tests.webfluxsse.dto.EventDTO;
import fr.an.tests.webfluxsse.dto.TopicListSubscribeRequestDTO;
import fr.an.tests.webfluxsse.dto.TopicSubscribeRequestDTO;
import lombok.val;
import reactor.core.publisher.Flux;

@Service
public class TopicService {

	// private final ReentrantLock lock = new ReentrantLock();
	private final Object lock = new Object();
	
	// @GuardedBy("lock")
	private final Map<String,TopicEntry> topics = new HashMap<>();
	
	public void publish(String topicName, String msg) {
		TopicEntry topicEntry = getOrCreateTopic(topicName);
		topicEntry.publish(msg);
	}

	public Flux<ServerSentEvent<EventDTO>> topicAsFlux(TopicSubscribeRequestDTO req) {
		TopicEntry topic = getOrCreateTopic(req.topic);
		return topic.filterThenFlux(req.lastEventId);
	}

	public Flux<ServerSentEvent<EventDTO>> topicLastEventThenFlux(String topicName) {
		TopicEntry topic = getOrCreateTopic(topicName);
		return topic.filterThenFlux(0);
	}

	private TopicEntry getOrCreateTopic(String topicName) {
		TopicEntry topicEntry;
		synchronized (lock) {
			topicEntry = topics.get(topicName);
			if (topicEntry == null) {
				topicEntry = new TopicEntry(topicName);
				topics.put(topicName, topicEntry);
			}
			
		}
		return topicEntry;
	}

	public EventDTO topicPublish(String topicName, String msg) {
		TopicEntry topic = getOrCreateTopic(topicName);
		return topic.publish(msg);
	}

	public EventDTO topicLastEvent(String topicName) {
		TopicEntry topic = getOrCreateTopic(topicName);
		return topic.getLastEvent();
	}

	public Map<String, EventDTO> topicLastEvents(List<String> topicNames) {
		Map<String, EventDTO> res = new LinkedHashMap<String, EventDTO>();
		for(val topicName : topicNames) {
			EventDTO event = topicLastEvent(topicName);
			res.put(topicName, event);
		}
		return res;
	}

	public Flux<ServerSentEvent<EventDTO>> topicListAsFlux(TopicListSubscribeRequestDTO req) {
		List<Flux<ServerSentEvent<EventDTO>>> sources = new ArrayList<>();
		for(val subscribe : req.subscribes) {
			Flux<ServerSentEvent<EventDTO>> source = topicAsFlux(subscribe);
			sources.add(source);
		}
		return Flux.merge(sources);
	}

}
