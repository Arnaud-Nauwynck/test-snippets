package fr.an.test.javaio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

public class SimultaenousWriteReadFileStreamTest {

	@Test
	public void test() throws IOException {
		File file = new File("target/test-simultaneous-read-write.txt");
		if (file.exists()) {
			file.delete();
		}
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		InputStream in = new BufferedInputStream(new FileInputStream(file));

		int writeCount = 0;
		int readCount = 0;
		
		byte[] write1 = "test1\n".getBytes();
		out.write(write1);
		writeCount += write1.length;
		out.flush();
		
		byte[] read1 = new byte[write1.length]; 
		in.read(read1);
		assertArrayEquals(write1, 0, read1, 0, write1.length);

		byte[] write2 = "test2\n".getBytes();
		out.write(write2);
		writeCount += write2.length;
		byte[] write3 = "test3\n".getBytes();
		out.write(write3);
		writeCount += write3.length;
		out.flush();
		
		byte[] read2 = new byte[write1.length]; 
		readCount += in.read(read2);
		assertArrayEquals(write2, 0, read2, 0, write2.length);

		byte[] read3 = new byte[write3.length]; 
		readCount += in.read(read3);
		assertArrayEquals(write3, 0, read3, 0, write3.length);

		in.close();
		out.close();
	}

	private static void assertArrayEquals(byte[] expected, int expectedPos, byte[] actual, int actualPos, int length) {
		for(int c = 0, i = expectedPos, j = actualPos; c < length; c++, i++,j++) {
			Assert.assertEquals(expected[i], actual[j]);
		}
	}
	
	
}
