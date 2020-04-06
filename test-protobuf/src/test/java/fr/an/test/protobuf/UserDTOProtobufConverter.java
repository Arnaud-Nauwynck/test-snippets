package fr.an.test.protobuf;

import fr.an.test.protobuf.UserProtos.User;
import lombok.val;

public class UserDTOProtobufConverter {

	public static User toProtoc(UserDTO src) {
		val b = UserProtos.User.newBuilder();
		b.setName(src.getName());
		if (src.getEmail() != null) {
			b.setEmail(src.getEmail());
		}
		b.setIntValue(src.getIntValue());
		
		val srcBestFriend = src.getBestFriend();
		if (srcBestFriend != null) {
			val bestFriend = toProtoc(srcBestFriend);
			b.setBestFriend(bestFriend);
		}

		val srcFriends = src.getFriends();
		if (srcFriends != null && !srcFriends.isEmpty()) {
			for(val f : srcFriends) {
				val friend = toProtoc(f);
				b.setFriends(friend);
			}
		}
		
		return b.build();
	}
}
