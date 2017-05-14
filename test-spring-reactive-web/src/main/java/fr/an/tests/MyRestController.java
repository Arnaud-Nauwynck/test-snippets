package fr.an.tests;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import fr.an.tests.chat.ChatHistoryService;
import fr.an.tests.chat.ChatRoomEntry;
import fr.an.tests.chat.ChatRoomEntry.ChatMessageEntry;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/app")
public class MyRestController {
	
	private static final Logger LOG = LoggerFactory.getLogger(MyRestController.class);
	
	@Autowired
	private ChatHistoryService chatHistoryService;
	
	/**
	 * to test...
	 * curl -X GET -H 'Accept: application/json' http://localhost:8080/app/helloParams
	 */
	@GetMapping("/helloParams")
	public Map<String,String> helloParams() {
		Map<String,String> res = new HashMap<>();
		res.put("hello", "world");
		return res;
	}
	
	@GetMapping("/health")
	public Map<String,String> health() {
		Map<String,String> res = new HashMap<>();
		res.put("status", "OK");
		return res;
	}

	
	
	public static class PostChatMessage {
		public String msg;
		public String onBehalfOf;
	}

	/**
	 * example to test:
	 * <PRE>
	 * curl -X POST -H 'Content-Type: application/json' http://localhost:8081/app/chat/room/Default --data-binary '{"msg":"Hello", "onBehalfOf":"me"}'
	 * </PRE>
	 */
	@PostMapping(path="/chat/room/{chatRoom}")
	public void postChatMessage(@PathVariable("chatRoom") String chatRoom, @RequestBody PostChatMessage msg) {
		ChatRoomEntry chatRoomEntry = chatHistoryService.getChatRoom(chatRoom);
        if (chatRoomEntry == null) {
        	return;
        }
        String from = msg.onBehalfOf; 
        if (from == null) {
        	from = "from (cf security..)"; 
        }
        LOG.info("receive msg chatRoom:" + chatRoom + " from:" + from + " msg: " + msg.msg);
        chatRoomEntry.addMsg(new Date(), from, msg.msg);
	}
	
	@GetMapping(path="/chat/room/{chatRoom}/messages")
    public List<ChatMessageEntry> getChatMessages(@PathVariable("chatRoom") String chatRoom, 
    		@RequestParam(required=false) @DateTimeFormat(iso=ISO.DATE) Date since, 
    		@RequestParam(required=false, defaultValue="200") int limit) {
        ChatRoomEntry chatRoomEntry = chatHistoryService.getChatRoom(chatRoom);
        if (chatRoomEntry == null) {
        	return Collections.emptyList();
        }
		return chatRoomEntry.listMessagesSince(since);
    }
	
	// Server-Sent Event using spring4
	// ------------------------------------------------------------------------
	
	/**
	 * example to test:
	 * <PRE>
	 * curl http://localhost:8081/app/chat/room/Default/subscribeMessagesSpring4
	 * </PRE>
	 * ... get results:
	 * <PRE>
	 * data:{"date":1494660812844,"from":"me","chatRoom":"Default","msg":"Hello"}
	 * data:{"date":1494660826345,"from":"me2","chatRoom":"Default","msg":"Hello2"}
	 * </PRE>
	 * 
	 */
	@GetMapping(path = "/chat/room/{chatRoom}/subscribeMessagesSpring4", produces = "text/event-stream")
    public SseEmitter subscribeChatMessages(
    		@PathVariable("chatRoom") String chatRoom,
    		@RequestHeader(name="last-event-id", required=false) String lastEventId) {
		ChatRoomEntry chatRoomEntry = chatHistoryService.getChatRoom(chatRoom);
        if (chatRoomEntry == null) {
        	return null;
        }
        LOG.info("subscribeMessagesSpring4 lastEventId:" + lastEventId);
        return chatRoomEntry.subscribeSpring4(lastEventId);
    }

	// Server-Sent Event using spring5
	// ------------------------------------------------------------------------
	
	/**
	 * example to test:
	 * <PRE>
	 * curl http://localhost:8081/app/chat/room/Default/subscribeMessagesSpring5
	 * </PRE>
	 * ... get results:
	 * <PRE>
	 * id:1
	 * data:{"date":1494702169915,"from":"BOT","chatRoom":"Default","msg":"server start"}
	 * 
	 * id:2
	 * data:{"date":1494702213058,"from":"me","chatRoom":"Default","msg":"Hello"}
	 * 
	 * id:3
	 * data:{"date":1494702214060,"from":"me","chatRoom":"Default","msg":"Hello"}
	 * </PRE>
	 * 
	 */
	@GetMapping(path = "/chat/room/{chatRoom}/subscribeMessagesSpring5", produces = "text/event-stream")
    public Flux<ServerSentEvent<ChatMessageEntry>> subscribeChatMessages_spring5(
    		@PathVariable("chatRoom") String chatRoom,
    		@RequestHeader(name="last-event-id", required=false) String lastEventId) {
		ChatRoomEntry chatRoomEntry = chatHistoryService.getChatRoom(chatRoom);
        if (chatRoomEntry == null) {
        	return null;
        }
        LOG.info("subscribeMessagesSpring5 lastEventId:" + lastEventId);
        return chatRoomEntry.subscribeSpring5(lastEventId);
    }

	@ExceptionHandler(value = AsyncRequestTimeoutException.class)  
    public String asyncTimeout(AsyncRequestTimeoutException e){  
        return null; // "SSE timeout..OK";  
    }
	

}
