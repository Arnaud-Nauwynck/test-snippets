package fr.an.tests.chat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import fr.an.tests.chat.ChatRoomEntry.ChatMessageEntry;

@Component
public class ChatHistoryService {

	public Map<String,ChatRoomEntry> chatRooms = new HashMap<>();
	
	public ChatHistoryService() {
		ChatRoomEntry defaultChatRoom = addChatRoom("Default");
		defaultChatRoom.addMsg(new ChatMessageEntry(new Date(), "BOT", "Default", "server start"));
	}
	
	public ChatRoomEntry addChatRoom(String name) {
		ChatRoomEntry res = chatRooms.get(name);
		if (res == null) {
			res = new ChatRoomEntry(name);
			chatRooms.put(name, res);
		}
		return res;
	}
	
	public void removeChatRoom(String name) {
		chatRooms.remove(name);
	}
	
	public ChatRoomEntry getChatRoom(String name) {
		return chatRooms.get(name);
	}
	
}
