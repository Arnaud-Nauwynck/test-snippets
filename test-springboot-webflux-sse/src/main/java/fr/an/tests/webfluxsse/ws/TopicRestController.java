package fr.an.tests.webfluxsse.ws;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.tests.webfluxsse.dto.EventDTO;
import fr.an.tests.webfluxsse.dto.EventPublishRequestDTO;
import fr.an.tests.webfluxsse.dto.LastEventRequestDTO;
import fr.an.tests.webfluxsse.dto.LastEventTopicListRequestDTO;
import fr.an.tests.webfluxsse.dto.TopicGetLastThenSubscribeRequestDTO;
import fr.an.tests.webfluxsse.dto.TopicListSubscribeRequestDTO;
import fr.an.tests.webfluxsse.dto.TopicSubscribeRequestDTO;
import fr.an.tests.webfluxsse.service.TopicService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/topic")
public class TopicRestController {

	@Autowired
	private TopicService topicService;

	/**
	 * example to test:
	 * <PRE>
	 * curl -H 'content-type:application/json' -H 'accept:application/json' -X POST http://localhost:8080/api/topic/publish -d '{ "topic":"t1", "msg":"hello msg" }'
	 * </PRE>
	 */
	@PostMapping(path = "/publish")
    public EventDTO publish(
    		@RequestBody EventPublishRequestDTO req) {
		return topicService.topicPublish(req.topic, req.msg);
    }

	/**
	 * example to test:
	 * curl -H 'content-type:application/json' -H 'accept:application/json' -X POST http://localhost:8080/api/topic/lastEvent -d '{ "topic":"t1" }'
	 */
	@PostMapping(path = "/lastEvent")
    public EventDTO lastEvent(
    		@RequestBody LastEventRequestDTO req) {
		return topicService.topicLastEvent(req.topic);
    }
	
	/**
	 * example to test:
	 * <PRE>
	 * curl -H 'content-type:application/json' -H 'accept:application/json' -X POST http://localhost:8080/api/topic/lastEvents -d '{ "topics":[ "t1", "t2" ] }'
	 * ..
	 * {"t1":{"topic":"t1","messageId":1,"msg":"hello msg 4"},"t2":null}
	 * </PRE>
	 */
	@PostMapping(path = "/lastEvents")
    public Map<String,EventDTO> lastEvents(
    		@RequestBody LastEventTopicListRequestDTO req) {
		return topicService.topicLastEvents(req.topics);
    }
	
	// listen to Server-Sent-Events
	// ------------------------------------------------------------------------


	/**
	 * example to test:
	 * <PRE>
	 * curl -H 'content-type: application/json' -X PUT http://localhost:8080/api/topic/lastEventThenSubscribe -d '{ "topic":"t1" }'
	 * </PRE>
	 */
	@PutMapping(path = "/lastEventThenSubscribe", produces = "text/event-stream")
    public Flux<ServerSentEvent<EventDTO>> getAndSubscribe(
    		@RequestBody TopicGetLastThenSubscribeRequestDTO req) {
		return topicService.topicLastEventThenFlux(req.topic);
    }

	/**
	 * example to test:
	 * <PRE>
	 * curl -H 'content-type: application/json' -X PUT http://localhost:8080/api/topic/subscribe -d '{ "topic":"t1", "lastEventId":2 }'
	 * </PRE>
	 * 
	 * ... example to launch 100 subscribers in backgrounds... and check that there is NO additionnal thread created on server:
	 * <PRE>
	 * for i in $(seq 1 1000); do; ( curl -H 'content-type: application/json' -X PUT http://localhost:8080/api/topic/subscribe -d '{ "topic":"t1", "lastEventId":2 }' &); done
	 * </PRE>
	 *
	 * .. example to get event limit to 5s
	 * curl -m 5 -H 'content-type: application/json' -X PUT http://localhost:8080/api/topic/subscribe -d '{ "topic":"t1", "lastEventId": 2}'
     * id:msg#11
     * event:topicEvent
     * data:{"topic":"t1","messageId":11,"msg":"hello msg 4"}
     * 
     * curl: (28) Operation timed out after 5000 milliseconds with 83 bytes received
     * 
     * .. to avoid last curl error msg: "curl -m 5 ...  2> /dev/null"
     * curl -m 5 -H 'content-type: application/json' -X PUT http://localhost:8080/api/topic/subscribe -d '{ "topic":"t1", "lastEventId": 2}'  2> /dev/null 
	 */
	@PutMapping(path = "/subscribe", produces = "text/event-stream")
    public Flux<ServerSentEvent<EventDTO>> subscribe(
    		@RequestBody TopicSubscribeRequestDTO req) {
		return topicService.topicAsFlux(req);
    }

	/**
	 * example to test:
	 * <PRE>
	 * curl -H 'content-type: application/json' -X PUT http://localhost:8080/api/topic/subscribeList -d '{ "subscribes":[ { "topic":"t1", "lastEventId":2 }, { "topic":"t2", "lastEventId":3 } ] }'
	 * 
	 * ..
	 * id:msg#1
     * event:topicEvent
     * data:{"topic":"t1","messageId":1,"msg":"hello msg topic1"}
     * 
     * id:msg#1
     * event:topicEvent
     * data:{"topic":"t2","messageId":1,"msg":"hello msg topic2"}
     * </PRE>
	 */
	@PutMapping(path = "/subscribeList", produces = "text/event-stream")
    public Flux<ServerSentEvent<EventDTO>> subscribeList(
    		@RequestBody TopicListSubscribeRequestDTO req) {
		return topicService.topicListAsFlux(req);
    }
}
