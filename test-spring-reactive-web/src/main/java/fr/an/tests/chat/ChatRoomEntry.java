package fr.an.tests.chat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;

public class ChatRoomEntry {

	public static class ChatMessageEntry {
		public final Date date;
		public final String from;
		public final String chatRoom; // redundant
		public final String msg;
		
		public ChatMessageEntry(Date date, String from, String chatRoom, String msg) {
			this.date = date;
			this.from = from;
			this.chatRoom = chatRoom;
			this.msg = msg;
		}
		
	}
	
	public final String name;
	private List<ChatMessageEntry> messages = new ArrayList<>();
	
	private List<ChatRoomMessageListener> listeners = new ArrayList<>();
	private Spring4ChatRoomSSE spring4SSE;
	private Spring5ChatRoomSSE spring5SSE;
	
	// ------------------------------------------------------------------------
	
	public ChatRoomEntry(String name) {
		this.name = name;
		this.spring4SSE = new Spring4ChatRoomSSE(this);
		listeners.add(spring4SSE);
		this.spring5SSE = new Spring5ChatRoomSSE(this);
		listeners.add(spring5SSE);
	}

	// ------------------------------------------------------------------------

	public void addMsg(ChatMessageEntry p) {
		messages.add(p);
		for(ChatRoomMessageListener listener : listeners) {
			listener.onPostMessage(p);
		}
	}
	
	public List<ChatMessageEntry> listMessagesSince(Date since) {
		return messages.stream()
				.filter(x -> since == null || x.date.after(since))
				.collect(Collectors.toList());
	}

	public SseEmitter subscribeSpring4() {
		return spring4SSE.subscribe();
	}

	public Flux<ChatMessageEntry> subscribeSpring5() {
		return spring5SSE.subscribe();
	}

}