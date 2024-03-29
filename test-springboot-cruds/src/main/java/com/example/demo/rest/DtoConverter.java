package com.example.demo.rest;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.badmerge.BadMergeReferenceEntity;
import com.example.demo.badmerge.BadMergeSourceDTO;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public class DtoConverter {
	
	private MapperFacade mapper = createMapper();
	
	private MapperFacade createMapper() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		return mapperFactory.getMapperFacade();
	}
	
	public <S, D> D map(S sourceObject, Class<D> destinationClass) {
		return mapper.map(sourceObject, destinationClass);
	}
	
	public <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
		return mapper.mapAsList(source, destinationClass);
	}

	public <S, D> void map(S source, D dest) {
		mapper.map(source, dest);
	}
	
}
