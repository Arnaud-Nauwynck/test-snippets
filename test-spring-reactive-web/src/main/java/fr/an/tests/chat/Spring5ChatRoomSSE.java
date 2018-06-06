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
	
	public Spring5ChatRoomSSE(ChatRoomEntry chatRoom) {
		this.chatRoom = chatRoom;
		this.replayProcessor = ReplayProcessor.<ServerSentEvent<ChatMessageEntry>>create(100);
	}

	@Override
	public void onPostMessage(ChatMessageEntry msg) {
		ServerSentEvent<ChatMessageEntry> event = ServerSentEvent.builder(msg)
				.event("chat")
				.id(Integer.toString(msg.id)).build();
		replayProcessor.onNext(event);
	}

	public Flux<ServerSentEvent<ChatMessageEntry>> subscribe(String lastEventId) {
		Integer lastId = (lastEventId != null)? Integer.parseInt(lastEventId) : null;
		return replayProcessor.filter(x -> lastId == null || x.data().get().id > lastId);
	}

}
