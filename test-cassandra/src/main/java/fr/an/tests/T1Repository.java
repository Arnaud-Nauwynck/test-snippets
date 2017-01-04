package fr.an.tests;

import java.util.List;

import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.TypedIdCassandraRepository;

public interface T1Repository extends TypedIdCassandraRepository<T1,T1Key> {

	@Query("select * from T1 where f1=?0")
	public List<T1> findByF1(int f1);

}
