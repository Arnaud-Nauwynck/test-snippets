package fr.an.test.protobuf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.an.test.protobuf.UserProtos.User;
import lombok.val;

public class ProtobufTest {
	UserDTO user1, user2;
	User user1Proto, user2Proto;
	
	@Before
	public void setup() {
		user1 = new UserDTO();
		user1.setName("Me");
		user1.setEmail("me@gmail");
		user1.setIntValue(123);

		user2 = new UserDTO();
		user2.setName("You");
		user2.setEmail("You@gmail");
		user2.setIntValue(123);

		user1.setBestFriend(user2);
		// user2.setBestFriend(user1); // cycle! => StackOverflow .. NOT supported by protobuf !!
		
		user1Proto = UserDTOProtobufConverter.toProtoc(user1);
		user2Proto = user1Proto.getBestFriend();
	}

	@Test
	public void testUsers() throws Exception {
		val data = user1Proto.toByteArray();
		System.out.println("user1 data.length:" + data.length);
		val user1Copy = User.parseFrom(data);
		
		Assert.assertEquals(user1.getName(), user1Copy.getName());
		Assert.assertEquals(user1.getEmail(), user1Copy.getEmail());
		User bestFriendCopy = user1Copy.getBestFriend();
		Assert.assertEquals(user1.getBestFriend().getName(), bestFriendCopy.getName());
		// cycle... NOT SUPPORTED Assert.assertSame(user1Copy.getFriend().getFriend(), user1Copy);
	}


}
