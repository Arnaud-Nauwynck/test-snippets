package fr.an.tests.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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
		for(SseEmitter emitter : emitters) {
			try {
				emitter.send(msg, MediaType.APPLICATION_JSON);
			} catch (IOException ex) {
				LOG.error("Failed to send msg to emitter", ex);
			}
		}
	}
	
	public SseEmitter subscribe() {
		SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        
        emitter.onCompletion(() -> {
        	LOG.info("onCompletion -> remove emitter");
        	emitters.remove(emitter);	
        });

        emitter.onTimeout(() -> {
        	LOG.info("onTimout -> remove emitter");
        	emitters.remove(emitter);	
        });

        return emitter;
	}

}
