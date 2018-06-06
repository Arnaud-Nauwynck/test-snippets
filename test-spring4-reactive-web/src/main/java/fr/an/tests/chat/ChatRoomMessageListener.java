package fr.an.tests.chat;

import fr.an.tests.chat.ChatRoomEntry.ChatMessageEntry;

public interface ChatRoomMessageListener {

	public void onPostMessage(ChatMessageEntry msg);
}
