package com.example.demo.directquery2dto;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.badmerge.BadMergeReferenceEntity;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class DirectQuery2DTOService {

	@Autowired
	private EntityManager em;
	
	public List<CustomQueryResultDTO> query1(String param) {
		String jpaQL = "SELECT new com.example.demo.directquery2dto.CustomQueryResultDTO(" //
				+ " p.id, p.field1, p.field2, " //
				+ " p.ref.id, p.ref.field1, p.ref.field2 " // <= field navigation, equivalent to Sql tables join
				+ " )" //
				+ " FROM DirectSourceEntity p " //
				+ " WHERE p.field1 LIKE :param"; // 
		TypedQuery<CustomQueryResultDTO> q = em.createQuery(jpaQL, CustomQueryResultDTO.class);
		q.setParameter("param", param);
		List<CustomQueryResultDTO> resDtos = q.getResultList();
		return resDtos;
	}

	
	@Autowired
	DirectSourceRepository sourceRepo;

	@Autowired
	DirectReferenceRepository referenceRepo;

	public void initDb() {
		for(long id = 0; id < 10; id++) {
			Optional<DirectSourceEntity> refOpt = sourceRepo.findById(id);
			if (! refOpt.isPresent()) {
				DirectSourceEntity sourceEntity = new DirectSourceEntity();
				sourceEntity.setField1("field1");
				sourceEntity.setField2("field2");
				
				DirectReferenceEntity ref = new DirectReferenceEntity();
				ref.setField1("field1");
				ref.setField2("field2");
				ref.setField3("field3");
				ref.setField4("field4");
				
				ref = referenceRepo.save(ref); // no cascade create, need explicit..
				sourceEntity.setRef(ref);
				
				sourceRepo.save(sourceEntity);
			}
		}
	}
	
}
