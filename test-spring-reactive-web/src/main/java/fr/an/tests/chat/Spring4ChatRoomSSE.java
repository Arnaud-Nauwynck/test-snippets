package fr.an.tests.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

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
		if (emitters.isEmpty()) {
			return;
		}
		SseEventBuilder evtBuilder = SseEmitter.event()
				.id(Integer.toString(msg.id))
				.name("chat")
				.data(msg);
		for(SseEmitter emitter : emitters) {
			try {
				emitter.send(evtBuilder);
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
				LOG.error("Failed to re-send msg to emitter, ex:", ex.getMessage() + " => complete with error ... remove,disconnect");
				emitter.completeWithError(ex);
			}
		}
		
        emitters.add(emitter);
        
        emitter.onCompletion(() -> {
        	LOG.info("onCompletion -> remove emitter");
        	emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
        	LOG.info("onTimeout -> remove emitter");
        	emitters.remove(emitter);
        });

        return emitter;
	}

}
