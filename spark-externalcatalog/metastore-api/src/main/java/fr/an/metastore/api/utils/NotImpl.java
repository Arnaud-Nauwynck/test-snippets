package fr.an.metastore.api.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotImpl {

	public static UnsupportedOperationException notImplEx() {
		log.error("NOT IMPLEMENTED YET ... throwing ex");
		throw new UnsupportedOperationException("NOT IMPL"); // TODO
	}
}
