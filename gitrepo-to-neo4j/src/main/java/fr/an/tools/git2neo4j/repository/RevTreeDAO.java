package fr.an.tools.git2neo4j.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import fr.an.tools.git2neo4j.domain.RevTreeEntity;

public interface RevTreeDAO extends GraphRepository<RevTreeEntity> {

	@Query("match(t) where t.sha1 in ({0}) return t")
	List<RevTreeEntity> findBySHA1s(Collection<String> sha1s);
	
	@Query("match(root:DirTree{sha1:{0}}) -[:childFile*]-> (child:) return child") // (DirTree|Blob|SymLink|GitLink)
	List<RevTreeEntity> findRecursiveChildFileBySHA1(String sha1);
	
}
