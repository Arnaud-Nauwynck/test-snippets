package fr.an.testhibernatejpafk.service;

import java.util.List;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class DtoConverter {
	
	private static MapperFacade mapper = createMapper();
	
	private static MapperFacade createMapper() {
		MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		return mapperFactory.getMapperFacade();
	}
	
	public static <S, D> D map(S sourceObject, Class<D> destinationClass) {
		return mapper.map(sourceObject, destinationClass);
	}
	
	public static <S, D> List<D> mapAsList(Iterable<S> source, Class<D> destinationClass) {
		return mapper.mapAsList(source, destinationClass);
	}
	
}
