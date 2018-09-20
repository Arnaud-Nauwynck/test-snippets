package fr.an.tests.testgraphqlsrv;

import java.io.File;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//@Component // TODO unused.. cf explicit Query resolver
public class JsonRootQueryResolver implements GraphQLQueryResolver {

	private JsonNode db1Data;
	private ObjectMapper objectMapper = new ObjectMapper(); 
	
	public JsonRootQueryResolver() {
		File jsonFile = new File("data/db1Data.json");
		try {
			this.db1Data = objectMapper.readTree(jsonFile);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read json file " + jsonFile, ex);
		}
	}

	/** called by introspection: root query resolver */
	public JsonNode db1Data() {
		return db1Data;
	}
	
}
