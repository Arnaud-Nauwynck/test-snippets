package fr.an.tests.hadoopfsinstrumented;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsCliTest {

	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration(false);
		conf.setQuietMode(false);
		// already added default resource: "core-default.xml", "core-site.xml"
		String hadoopConf = System.getenv("HADOOP_CONF");
		if (hadoopConf == null) {
			String hadoopHome = System.getenv("HADOOP_HOME");
			if (hadoopHome != null) {
				hadoopConf = hadoopHome + "/conf";
			}
		}
		if (hadoopConf != null) {
			conf.addResource(new Path(hadoopConf + "/core-site.xml"));
		}
		
		String defaultFs = conf.get("fs.defaultFS", "fs1:///TODO-default");
		System.out.println("fs.defaultFs: " + defaultFs);
		
		FileSystem fs = FileSystem.get(conf);
		Path dir1Path = new Path("/dir1");
		if (!fs.exists(dir1Path)) {
			fs.mkdirs(dir1Path);
		}
		Path file1Path = new Path("/dir1/file1.txt");
		
		if (!fs.exists(file1Path)) {
			try (FSDataOutputStream out = fs.create(file1Path, true)) {
				out.write("test line1\n".getBytes());
			}
		}
		
		// read
		try (FSDataInputStream in = fs.open(file1Path)) {
			byte[] buffer = new byte[4096];
			for(;;) {
				int readCount = in.read(buffer);
				if (readCount == -1) {
					break;
				}
			}
		}
		
		// write
		Path fileWritePath = new Path("/dir1/fileWrite.txt");
		try (FSDataOutputStream out = fs.create(fileWritePath, true)) {
			out.write("test line1\n".getBytes());
			out.flush();
		}	
		// write append
		try {
			try (FSDataOutputStream out = fs.append(fileWritePath)) {
				out.write("test line2\n".getBytes());
				out.flush();
			}
		} catch(UnsupportedOperationException ex) {
			if (ex.getMessage().equals("Append is not supported by ChecksumFileSystem")) {
				System.err.println("Append is not supported by ChecksumFileSystem?? ... skip append to file !");
			}
		}
	}

}
