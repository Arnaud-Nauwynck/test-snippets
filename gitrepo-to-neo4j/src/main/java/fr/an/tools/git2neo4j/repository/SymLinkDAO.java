package fr.an.tools.git2neo4j.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import fr.an.tools.git2neo4j.domain.SymLinkEntity;

public interface SymLinkDAO extends GraphRepository<SymLinkEntity> {

}
