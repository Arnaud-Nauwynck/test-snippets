package fr.an.tests.springbootspel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder @AllArgsConstructor
@Slf4j
public class Foo {
	int field1;
	
	public int fooFunc(int x) {
		log.info("fooFunc(" + x + ")");
		return x+1;
	}

}