package fr.an.tests;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestCassandraApplication implements CommandLineRunner {

	@Autowired
	private T1Repository repository;

	@Value("${spring.data.cassandra.keyspace-name}")
	private String cassandraKeyspace;
	
	public static void main(String[] args) {
		SpringApplication.run(TestCassandraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("using Cassandra keyspace:" + cassandraKeyspace);
		
		long countAll = repository.count();
		System.out.println("count -> " + countAll + " elt(s)");
		Iterable<T1> all= repository.findAll();
		System.out.println("findAll -> [0]:" + all.iterator().next() + " ...");
		
		T1 res00 = repository.findById(new T1Key(0, 0)).orElse(null);
		System.out.println("find pk:(0,0) -> " + res00);
		
		List<T1> resLs = repository.findByF1(0);
		System.out.println("find by f1=0 -> " + resLs.size() + " elt(s)");
		
		int maxId = resLs.stream().mapToInt(x -> x.getPK().getF1()).max().getAsInt();
		T1Key newPK = new T1Key(maxId+1, 0);
		T1 newT1 = new T1(newPK);
		newT1.setF3("test from springboot-data");
		repository.save(newT1);
	}
}
