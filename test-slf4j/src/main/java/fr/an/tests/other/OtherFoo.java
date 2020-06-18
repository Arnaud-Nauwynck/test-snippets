package fr.an.tests.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtherFoo {

	private static final Logger log = LoggerFactory.getLogger(OtherFoo.class);

	public static void foo() {
		log.info("msg from other package..");
	}
}