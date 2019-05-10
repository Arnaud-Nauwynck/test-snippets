package fr.an.testhdfs;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HdfsCli {

	public static void main(String[] args) throws IOException {
		String hadoopHome = "D:\\arn\\hadoop\\hadoop-2.6.5";
		System.setProperty("hadoop.home.dir", hadoopHome); // for winutils.exe + config..
		
		Configuration conf = new Configuration();
		File hadoopConf = new File(hadoopHome + "\\etc\\hadoop");
		conf.addResource(new File(hadoopConf, "core-site.xml").toURI().toURL());
		conf.addResource(new File(hadoopConf, "hdfs-site.xml").toURI().toURL());

//		conf.set("fs.defaultFS", "hdfs://localhost:20112");
		
		
		FileSystem fs = FileSystem.get(conf);
		Path dir1Path = new Path("/dir1");
		if (!fs.exists(dir1Path)) {
			fs.mkdirs(dir1Path);
		}
		Path file1Path = new Path("/dir1/file1.txt");
		if (!fs.exists(file1Path)) {
			try (FSDataOutputStream out = fs.create(file1Path, false)) {
				out.write("test line1\n".getBytes());
			}
		}
		
	}
}
