package fr.an.tests.springbootspel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyStaticFuncs {
	
	public static int resolved_myCustomFoo(int x) {
		log.info("resolved_myCustomFoo(" + x + ")");
		return x+1;
	}
	
	public static int customF(int x) {
		log.info("customF(" + x + ")");
		return x+1;
	}
	
}