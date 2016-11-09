package fr.an.tools.git2neo4j.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import fr.an.tools.git2neo4j.domain.RevCommitEntity;

public interface RevCommitDAO extends GraphRepository<RevCommitEntity> {

}
