package fr.an.test.testpostgresqljpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FooRepository extends JpaRepository<FooEntity,Integer> {

}
