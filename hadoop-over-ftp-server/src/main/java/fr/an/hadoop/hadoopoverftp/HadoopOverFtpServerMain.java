package fr.an.hadoop.hadoopoverftp;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.impl.DefaultFtpServerContext;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import fr.an.hadoop.hadoopoverftp.impl.HadoopFtpFileSystemFactory;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Option;

@Slf4j
public class HadoopOverFtpServerMain {

	@Option(names = "--port", description = "ftp server port")
	private int port = 21;
		
	@Option(names = "--passive-ports", description = "ftp server passive port")
	private String passivePorts;
	
	@Option(names = "--hdfs-uri", description = "Hadoop hdfs uri")
	private String hdfsUri = "file:///";

	
	public static void main(String[] args) {
		try {
			HadoopOverFtpServerMain app = new HadoopOverFtpServerMain();
			new CommandLine(app).parseArgs(args);
			app.run();
		} catch(Exception ex) {
			System.err.println("Failed, exiting");
			ex.printStackTrace(System.err);
		}
	}

	private void run() {
		URI hadoopFsURI;
		try {
			hadoopFsURI = new URI(hdfsUri);
		} catch (URISyntaxException ex) {
			throw new RuntimeException("Failed", ex);
		}
		Configuration hadoopConf = new Configuration();
		
		FileSystem fs;
		try {
			fs = FileSystem.get(hadoopFsURI, hadoopConf);
		} catch (IOException ex) {
			throw new RuntimeException("Failed", ex);
		}
		FileSystemFactory fileSystemFactory = new HadoopFtpFileSystemFactory(fs, hdfsUri);
		
		startFtpServer(fileSystemFactory);	
	}
	
	public void startFtpServer(FileSystemFactory fileSystemFactory) {
		log.info("Starting Hadoop-Over-Ftp server port: " + port 
				+ " fsUri: " + hdfsUri);

		DefaultFtpServerContext serverContext = new DefaultFtpServerContext();
		serverContext.setFileSystemManager(fileSystemFactory);

		try {
			serverContext.createDefaultUsers();
		} catch (Exception e) {
		}
		
		ListenerFactory listenerFactory = new ListenerFactory();
		listenerFactory.setPort(port);

//		DefaultConnectionConfig connectionConfig = new DefaultConnectionConfig();
//		serverContext.setConnectionConfig(connectionConfig);

		DataConnectionConfigurationFactory dataConConfigFactory = new DataConnectionConfigurationFactory();
		if (null != passivePorts) {
			dataConConfigFactory.setPassivePorts(passivePorts);
		}
		
		listenerFactory.setDataConnectionConfiguration(dataConConfigFactory.createDataConnectionConfiguration());
		
		serverContext.addListener("default", listenerFactory.createListener());


		DefaultFtpServer server = new DefaultFtpServer(serverContext);


		try {
			server.start();
		} catch (FtpException ex) {
			throw new RuntimeException("Failed server.start()", ex);
		}
	}

}
