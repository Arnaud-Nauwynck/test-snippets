package fr.an.testwebsocket;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * using STOMP is the Simple (or Streaming) Text Orientated Messaging Protocol.
 *
 */
@Controller
@Slf4j
public class WebSocketController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private ObjectMapper jsonMapper;

	public static final String destinationChat = "/chat";
	public static final String destinationChatText = "/chatText";
	
	@Data @AllArgsConstructor @NoArgsConstructor
	public static class MessageDTO {
		public String strValue;
		public int intValue;
		public List<String> args;
	}
	
	@MessageMapping("/send/text")
	public void onReceiveMessageText(String msg) {
		log.info("onReceiveMessage " + msg);
		this.messagingTemplate.convertAndSend(destinationChatText, msg);
	}

	@MessageMapping("/send/message")
	public void onReceiveMessage(@Payload MessageDTO msg) throws Exception {
		log.info("onReceiveMessage " + msg);
		this.messagingTemplate.convertAndSend(destinationChat, msg);
	}
	
	@MessageMapping("/send/message2")
	public void onReceiveMessage2(@Payload String payload) throws Exception {
		MessageDTO msg = jsonMapper.readValue(payload, MessageDTO.class);
		
		log.info("onReceiveMessage " + msg);
		
		String sendAsText = jsonMapper.writeValueAsString(msg);
		this.messagingTemplate.convertAndSend(destinationChat, sendAsText);
	}
	
}
