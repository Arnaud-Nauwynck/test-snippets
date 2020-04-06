package fr.an.test.avro;

import java.io.File;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.Test;

import example.avro.User;

public class AvroTest {

	@Test
	public void testUsers() throws Exception {
		User user1 = new User();
		user1.setName("Alyssa");
		user1.setFavoriteNumber(256);
		// Leave favorite color null

		// Alternate constructor
		User user2 = new User("Ben", 7, "red");

		// Construct via builder
		User user3 = User.newBuilder().setName("Charlie").setFavoriteColor("blue").setFavoriteNumber(null).build();

		// Serialize user1, user2 and user3 to disk
		File file = new File("target/test-users.avro");
		DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
		try (DataFileWriter<User> out = new DataFileWriter<User>(userDatumWriter)) {
			out.create(user1.getSchema(), file);
			out.append(user1);
			out.append(user2);
			out.append(user3);
		}

		// Deserialize Users from disk
		DatumReader<User> userDatumReader = new SpecificDatumReader<User>(User.class);
		try (DataFileReader<User> dataFileReader = new DataFileReader<User>(file, userDatumReader)) {
			User user = null;
			while (dataFileReader.hasNext()) {
				user = dataFileReader.next(user);
				System.out.println(user);
			}
		}
	}
}
