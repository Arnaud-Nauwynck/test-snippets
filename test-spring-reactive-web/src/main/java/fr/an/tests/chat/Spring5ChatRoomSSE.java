package fr.an.tests.chat;

import java.util.List;
import java.util.function.Consumer;

import fr.an.tests.chat.ChatRoomEntry.ChatMessageEntry;
import reactor.core.publisher.Flux;

/**
 * example doc/tutorials:
 * https://github.com/spring-projects/spring-framework/blob/master/src/docs/asciidoc/web/web-flux.adoc
 *
 */
public class Spring5ChatRoomSSE implements ChatRoomMessageListener  {

	protected final ChatRoomEntry chatRoom;

	private Consumer<ChatMessageEntry> emitter;
	private List<Flux<ChatMessageEntry>> subscriptions;
	
	public Spring5ChatRoomSSE(ChatRoomEntry chatRoom) {
		this.chatRoom = chatRoom;
//		this.emitter = new ;
	}

	@Override
	public void onPostMessage(ChatMessageEntry msg) {
//		emitter.accept(msg);
	}

	public Flux<ChatMessageEntry> subscribe() {
//		Flux<ChatMessageEntry> res = Flux.<ChatMessageEntry>create(emmiter);
//		subscriptions.add(res);
//		
//		emitter.onTimeout(() -> this.emitters.remove(emitter));
//		
//		return res;
		return null;
	}

}
