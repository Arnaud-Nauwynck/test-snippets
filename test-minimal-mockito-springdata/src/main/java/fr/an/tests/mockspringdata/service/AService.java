package fr.an.tests.mockspringdata.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.an.tests.mockspringdata.domain.AEntity;
import fr.an.tests.mockspringdata.dto.ADTO;
import fr.an.tests.mockspringdata.repository.ARepo;
import lombok.AccessLevel;
import lombok.Setter;

@Component
@Transactional
public class AService {

	@Autowired
	@Setter(AccessLevel.PACKAGE)
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
