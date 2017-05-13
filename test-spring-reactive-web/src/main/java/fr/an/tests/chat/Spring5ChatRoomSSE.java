package fr.an.tests.chat;

import org.springframework.http.codec.ServerSentEvent;

import fr.an.tests.chat.ChatRoomEntry.ChatMessageEntry;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

/**
 * example doc/tutorials:
 * https://github.com/spring-projects/spring-framework/blob/master/src/docs/asciidoc/web/web-flux.adoc
 *
 */
public class Spring5ChatRoomSSE implements ChatRoomMessageListener  {

	protected final ChatRoomEntry chatRoom;

	private ReplayProcessor<ServerSentEvent<ChatMessageEntry>> replayProcessor;
	
	private int idGenerator = 1;
	
	public Spring5ChatRoomSSE(ChatRoomEntry chatRoom) {
		this.chatRoom = chatRoom;
		this.replayProcessor = ReplayProcessor.<ServerSentEvent<ChatMessageEntry>>create(100);
	}

	@Override
	public void onPostMessage(ChatMessageEntry msg) {
		ServerSentEvent<ChatMessageEntry> event = ServerSentEvent.builder(msg)
				// .event("chat")
				.id(generateNewId()).build();
		replayProcessor.onNext(event);
	}

	private String generateNewId() {
		return Integer.toString(idGenerator++);
	}

	public Flux<ServerSentEvent<ChatMessageEntry>> subscribe(String lastEventId) {
		return replayProcessor
				// ??? 
				.log(); //??  subscribe()
	}

}
