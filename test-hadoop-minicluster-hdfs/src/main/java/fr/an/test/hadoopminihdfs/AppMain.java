package fr.an.test.hadoopminihdfs;


import org.apache.hadoop.conf.Configuration;

import com.github.sakserv.minicluster.impl.HdfsLocalCluster;

public class AppMain {

	public static void main(String[] args) throws Exception {
		HdfsLocalCluster hdfsLocalCluster = new HdfsLocalCluster.Builder()
			    .setHdfsNamenodePort(20112)
			    .setHdfsNamenodeHttpPort(50070)
			    .setHdfsTempDir("embedded_hdfs")
			    .setHdfsNumDatanodes(1)
			    .setHdfsEnablePermissions(false)
			    .setHdfsFormat(false)
			    .setHdfsEnableRunningUserAsProxyUser(true)
			    .setHdfsConfig(new Configuration())
			    .build();
			                
		hdfsLocalCluster.start();
	}
}
