package fr.an.tests.jpaqlinmem.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.an.tests.jpaqlinmem.domain.AEntity;
import fr.an.tests.jpaqlinmem.dto.ADTO;
import fr.an.tests.jpaqlinmem.repository.ARepo;

@Component
@Transactional
public class AService {

	@Autowired
	private ARepo aRepo;
	
	@Autowired
	private DtoConverter converter;
	
	public ADTO findOneByName(String name) {
		AEntity tmpres = aRepo.findOneByName(name);
		return converter.map(tmpres, ADTO.class);
	}

	public List<ADTO> findByNameLike(String nameLike) {
		List<AEntity> tmpres = aRepo.findByNameLike(nameLike);
		return converter.mapAsList(tmpres, ADTO.class);
	}
	
}
