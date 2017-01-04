package fr.an.testcassandracli;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

public class AppMain {

	public static void main(String[] args) {
		new AppMain().run();
	}
	
	public void run() {
		// Connect to the cluster and keyspace "myks"
		Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
		Session session = cluster.connect("myks");
		System.out.println("connected to Cassandra localhost");
		
		// execute some queries...
		ResultSet results = session.execute("SELECT * FROM T1 WHERE f1=0");
		for (Row row : results) {
			int f1 = row.getInt("f1"); 
			int f2 = row.getInt("f2"); 
			String f3 = row.getString("f3"); 
			String f4 = row.getString("f4"); 
			System.out.println("row f1:" + f1 + ", f2:" + f2 + ", f3:'" + f3 + "', f4:'" + f4 + "'");
		}
		
	}
}
