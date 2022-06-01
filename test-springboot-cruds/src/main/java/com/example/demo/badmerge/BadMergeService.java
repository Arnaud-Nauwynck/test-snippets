package com.example.demo.badmerge;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.rest.DtoConverter;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class BadMergeService {

	@Autowired
	private BadMergeSourceRepository sourceRepo;

	@Autowired
	private BadMergeReferenceRepository referenceRepo;

	@Autowired
	private DtoConverter dtoConverter;

	
	public BadMergeSourceDTO get(long id) {
		val entity = sourceRepo.getById(id);
		return dtoConverter.map(entity, BadMergeSourceDTO.class);
	}

	public BadMergeReferenceDTO getRef(long id) {
		val entity = referenceRepo.getById(id);
		return dtoConverter.map(entity, BadMergeReferenceDTO.class);
	}

	
	// @PostConstruct
	public void initDb() {
		for(long id = 3; id < 10; id++) {
			Optional<BadMergeReferenceEntity> refOpt = referenceRepo.findById(id);
			if (! refOpt.isPresent()) {
				BadMergeReferenceEntity ref = new BadMergeReferenceEntity();
				ref.setDisplayName("display-name " + id);
				ref.setField1("field1");
				ref.setField2("field2");
				ref.setField3("field3");
				ref.setField4("field4");
				ref.setField5("field5");
				ref = referenceRepo.save(ref);
				log.info("created BadMergeReference id:" + ref.getId());
			}
		}
	}
	

	public BadMergeSourceDTO createBUG(BadMergeSourceDTO src) {
		BadMergeSourceEntity entity = new BadMergeSourceEntity();
		dtoConverter.map(src, entity); // copy all mappable fields for dto -> entity  ..
								//  BUG  referenceEntity filled EMPTY with Id only .. not merged
		
		entity = sourceRepo.save(entity);

		sourceRepo.flush(); // .. will be done anyway during commit
		
		return dtoConverter.map(entity, BadMergeSourceDTO.class); 
		                        // => BUG ... invalid DTO return from referenceEntity !! 
	}
	
	public BadMergeSourceDTO createBUG2(BadMergeSourceDTO src) {
		BadMergeSourceEntity entity = new BadMergeSourceEntity();
		dtoConverter.map(src, entity); // copy all mappable fields for dto -> entity
		
		
		//
		if (src.getRef() != null) {
			BadMergeReferenceEntity compareWithRef = referenceRepo.findById(src.getRef().getId()).orElse(null);
			
			if (entity.getRef() != null) {
				// ok? merged attached??
			} else {
				log.error("not occur.. cf mapper");
				BadMergeReferenceEntity badRef = new BadMergeReferenceEntity(); // should be findById .. in session
				entity.setRef(badRef);
			}
		} else { // src.getRef() == null
			if (entity.getRef() == null) {
				// ok? merged detached
			} else {
				log.error("not occur.. cf mapper");
				entity.setRef(null);
			}
		}
		
		entity = sourceRepo.save(entity);
		sourceRepo.flush(); // .. will be done after in commit
		
		// find again from cache/db
		entity = sourceRepo.getById(entity.getId());
		val repeatFindRef = entity.getRef();
		
		return dtoConverter.map(entity, BadMergeSourceDTO.class);
	}

	
	public BadMergeSourceDTO createOk(BadMergeSourceDTO src) {
		BadMergeSourceEntity entity = new BadMergeSourceEntity();
		dtoConverter.map(src, entity); // copy all mappable fields for dto -> entity
		
		BadMergeReferenceIdDTO srcRefDTO = src.getRef();
		// BUG should be ...    refEntity = (srcRefDTO != null)? referenceRepo.getById(srcRefDTO.getId()) : null;
		// workaround:
		BadMergeReferenceEntity refEntity = (srcRefDTO != null)? getByIdOrThrow(srcRefDTO.getId()) : null;
		entity.setRef(refEntity);
		
		entity = sourceRepo.save(entity);
		sourceRepo.flush(); // .. will be done after in commit
		
		return dtoConverter.map(entity, BadMergeSourceDTO.class);
	}

	private BadMergeReferenceEntity getByIdOrThrow(long id) {
		BadMergeReferenceEntity res = // referenceRepo.getById(id);
				// BUG in hibernate / h2 !! 
				// should throw 400 EntityNotFound if invalid input id
				referenceRepo.findById(id).orElse(null);
		if (res == null) {
			throw new EntityNotFoundException("ReferenceEntity not found for invalid input id: " + id);
		}
		return res;
	}
	
	

	public BadMergeSourceDTO update(BadMergeSourceDTO srcDto) {
		val entity = sourceRepo.getById(srcDto.getId());
		dtoConverter.map(srcDto, entity);
		if (srcDto.getRef() == null) {
			if (entity.getRef() == null) {
				// ok 
			} else {
				log.error("not occur => detach existing");
			}
		} else {
			if (entity.getRef() != null) {
				// ok 
			} else {
				log.error("should not occur => attach existing");
			}
		}
		return dtoConverter.map(entity, BadMergeSourceDTO.class);
	}

}
