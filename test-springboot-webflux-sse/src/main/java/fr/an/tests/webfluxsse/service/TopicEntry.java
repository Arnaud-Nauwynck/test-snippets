package fr.an.tests.webfluxsse.service;

import org.springframework.http.codec.ServerSentEvent;

import fr.an.tests.webfluxsse.dto.EventDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

public class TopicEntry {

	private final String topic;
	private final Object lock = new Object();
	private int lastId = 0;
	private EventDTO lastEvent = null;
	
	private Many<ServerSentEvent<EventDTO>> sinkEvent = Sinks.many().replay().limit(1);
	
	public TopicEntry(String topic) {
		this.topic = topic;
	}


	public EventDTO publish(String msg) {
		int id;
		synchronized (lock) {
			id = ++lastId;

			EventDTO event = new EventDTO();
			event.topic = topic;
			event.messageId = id;
			event.msg = msg;
			this.lastEvent = event;
			
			ServerSentEvent<EventDTO> sseEvent = ServerSentEvent.<EventDTO>builder()
					.id("msg#"+id)
					.event("topicEvent") 
					.data(event)
					.build();
			sinkEvent.emitNext(sseEvent, null);
		
			return event;
		}
	}

	public EventDTO getLastEvent() {
		synchronized (lock) {
			return lastEvent;
		}
	}


	public Flux<ServerSentEvent<EventDTO>> replayThenFlux() {
		return sinkEvent.asFlux();
	}
	
	public Flux<ServerSentEvent<EventDTO>> filterThenFlux(int lastMsgId) {
		// TODO... 
		synchronized (lock) {
			if (this.lastEvent != null) {
				if (lastEvent.messageId == lastMsgId) {
					// do not resend
					return sinkEvent.asFlux().skip(1);
				} else {
					return sinkEvent.asFlux();
				}
			} else {
				return sinkEvent.asFlux();
			}
		}
	}



}
