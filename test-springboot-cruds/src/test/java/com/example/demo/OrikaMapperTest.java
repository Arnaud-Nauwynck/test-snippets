package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.example.demo.domain.TodoEntity;
import com.example.demo.rest.TodoDTO;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.CodeGenerationStrategy;
import ma.glasnost.orika.impl.generator.CompilerStrategy;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;

public class OrikaMapperTest {

	@Test
	public void testMap() {
		System.setProperty("ma.glasnost.orika.writeSourceFiles", "true");
		val mapperFactoryBuilder = new DefaultMapperFactory.Builder();
		
		MapperFactory mapperFactory = mapperFactoryBuilder.build();
		MapperFacade mapperFacade = mapperFactory.getMapperFacade();

		TodoEntity src = new TodoEntity();
		src.setId(1);
		src.setLabel("learn orika");
		
		// first convert => generate bytecode for converter(+ writeSourceFiles=true)
		TodoDTO resDTO = mapperFacade.map(src, TodoDTO.class);

		Assertions.assertEquals(src.getId(), resDTO.id);
		
		// second convert => use cached converter (classLoader class)
		resDTO = mapperFacade.map(src, TodoDTO.class);
	}
	
	/**
	 * see with opm.xml dependency
	 * <PRE>
	 * 	<dependency>
	 * 		<groupId>ma.glasnost.orika</groupId>
   	 * 		<artifactId>orika-eclipse-tools</artifactId>
	 * 		<version>${orika.version}</version>
	 * 	</dependency>
	 * </PRE>
	 */
	@Test
	public void testMap2() {
		System.setProperty("ma.glasnost.orika.writeSourceFiles", "true");
		val mapperFactoryBuilder = new DefaultMapperFactory.Builder();
		
		// java -Dma.glasnost.orika.compilerStrategy=ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy.EclipseJdtCompilerStrategy
		// or equivalent..
		System.setProperty("ma.glasnost.orika.compilerStrategy", 
				"ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy.EclipseJdtCompilerStrategy");
		// or equivalent.. 
		mapperFactoryBuilder.compilerStrategy(new EclipseJdtCompilerStrategy());
		
		MapperFactory mapperFactory = mapperFactoryBuilder.build();
		MapperFacade mapperFacade = mapperFactory.getMapperFacade();

		TodoEntity src = new TodoEntity();
		src.setId(1);
		src.setLabel("learn orika");
		
		// first convert => generate bytecode for converter(+ writeSourceFiles=true)
		TodoDTO resDTO = mapperFacade.map(src, TodoDTO.class);

		Assertions.assertEquals(src.getId(), resDTO.id);
		
		// second convert => use cached converter (classLoader class)
		resDTO = mapperFacade.map(src, TodoDTO.class);
	}
}
