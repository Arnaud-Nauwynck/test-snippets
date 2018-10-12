package fr.an.tests.jpaqlinmem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.an.tests.jpaqlinmem.domain.AEntity;

public interface ARepo extends JpaRepository<AEntity,Integer> {

	AEntity findOneByName(String name);
	
	List<AEntity> findByNameLike(String nameLike);
	
}
