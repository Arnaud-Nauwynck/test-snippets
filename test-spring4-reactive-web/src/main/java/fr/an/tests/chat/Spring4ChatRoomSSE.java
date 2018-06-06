package fr.an.tests.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import fr.an.tests.chat.ChatRoomEntry.ChatMessageEntry;

/**
 * example of docs/tutorials:
 * https://golb.hplar.ch/p/Server-Sent-Events-with-Spring
 *
 */
public class Spring4ChatRoomSSE implements ChatRoomMessageListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(Spring4ChatRoomSSE.class);
	
	protected final ChatRoomEntry chatRoom;

	private List<SseEmitter> emitters = new ArrayList<>();

	public Spring4ChatRoomSSE(ChatRoomEntry chatRoom) {
		this.chatRoom = chatRoom;
	}

	@Override
	public void onPostMessage(ChatMessageEntry msg) {
//		Set<DataWithMediaType> dataToSend = SseEmitter.event()
//				.id(Integer.toString(msg.id))
//				// .name("chat")
//				// .data(msg)
//				// .data(msg, null) // MediaType.APPLICATION_JSON
//				.data(msg)
//				.build();
//		for(SseEmitter emitter : emitters) {
//			try {
//				emitter.send(dataToSend);
//			} catch (IOException ex) {
//				LOG.error("Failed to send msg to emitter", ex);
//			}
//		}
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(msg);
			} catch (IOException ex) {
				LOG.error("Failed to send msg to emitter", ex);
			}
		}
	}
	
	public SseEmitter subscribe(String lastEventIdText) {
		SseEmitter emitter = new SseEmitter();
        Integer lastId = lastEventIdText != null? Integer.parseInt(lastEventIdText) : null;

        // TODO should lock:   listMessage() .... add() subscribe !
		List<ChatMessageEntry> replayMsgs = (lastId != null)?  
				chatRoom.listMessagesSinceLastId(lastId) : chatRoom.listMessages();
		for(ChatMessageEntry msg : replayMsgs) {
			if (lastId != null && msg.id <= lastId) {
				continue;
			}
			try {
				emitter.send(msg);
			} catch (IOException ex) {
				LOG.error("Failed to re-send msg to emitter", ex);
			}
		}
		
        emitters.add(emitter);
        
        emitter.onCompletion(() -> {
        	LOG.info("onCompletion -> remove emitter");
        	emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
        	LOG.info("onTimeout -> ?? remove emitter");
        });

        return emitter;
	}

}
