package fr.an.test.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoTest {
	User user1, user2;
	Kryo kryo;
	
	@Before
	public void setup() {
		user1 = new User();
		user1.setName("Me");
		user1.setPubFirstName("Me");
		user1.setIntValue(123);

		user2 = new User();
		user2.setName("You");
		user2.setPubFirstName("You");
		user2.setIntValue(123);

		user1.setFriend(user2);
		user2.setFriend(user1); // cycle!
		
		
		kryo = new Kryo();

	}

	@Test
	public void testUsers() throws Exception {
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		try (Output output = new Output(outBuffer)) {
			kryo.writeObject(output, user1);
		}
		
		User user1Copy;
		InputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
		try (Input in = new Input(inBuffer)) {
			user1Copy = kryo.readObject(in, User.class);
		}

		Assert.assertEquals(user1.getName(), user1Copy.getName());
		Assert.assertEquals(user1.getFriend().getName(), user1Copy.getFriend().getName());
		Assert.assertSame(user1Copy.getFriend().getFriend(), user1Copy);
	}

	@Test
	public void testUsers2() throws Exception {
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		try (Output output = new Output(outBuffer)) {
			kryo.writeObject(output, user1);
			kryo.writeObject(output, user1); // autoReset = true; .... => reset() => new reference
		}
		
		User user1Copy, user1CopyAgain;
		InputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
		try (Input in = new Input(inBuffer)) {
			user1Copy = kryo.readObject(in, User.class);
			user1CopyAgain = kryo.readObject(in, User.class);
		}

		Assert.assertEquals(user1.getName(), user1Copy.getName());
		Assert.assertEquals(user1.getFriend().getName(), user1Copy.getFriend().getName());
		Assert.assertSame(user1Copy.getFriend().getFriend(), user1Copy);

		Assert.assertEquals(user1.getName(), user1CopyAgain.getName());
		Assert.assertEquals(user1.getFriend().getName(), user1CopyAgain.getFriend().getName());
		Assert.assertSame(user1CopyAgain.getFriend().getFriend(), user1CopyAgain);

		Assert.assertNotSame(user1Copy, user1CopyAgain); // because autoReset = true;
	}

	@Test
	public void testUsers2_noAautoReset() throws Exception {
		kryo.setAutoReset(false);
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		try (Output output = new Output(outBuffer)) {
			kryo.writeObject(output, user1);
			kryo.writeObject(output, user1); // autoReset = false; .... no reset() => same reference
		}
		
		User user1Copy, user1CopyAgain;
		InputStream inBuffer = new ByteArrayInputStream(outBuffer.toByteArray());
		try (Input in = new Input(inBuffer)) {
			user1Copy = kryo.readObject(in, User.class);
			user1CopyAgain = kryo.readObject(in, User.class);
		}

		Assert.assertEquals(user1.getName(), user1Copy.getName());
		Assert.assertEquals(user1.getFriend().getName(), user1Copy.getFriend().getName());
		Assert.assertSame(user1Copy.getFriend().getFriend(), user1Copy);

		Assert.assertSame(user1Copy, user1CopyAgain); // because autoReset = false;
	}

}
