package fr.an.tests.testgraphqlsrv;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLSchemaProvider;

@RestController
public class GraphQLRestController {
	
	@Autowired
	GraphQLSchemaProvider graphQLSchemaProvider;
	
	// TODO redundant with SimpleGraphQLServlet... but pb with explicit resolver 
	@RequestMapping(value = "/graphql", method = RequestMethod.POST)
	public Map<String, Object> myGraphql(@RequestBody String request) throws Exception {
	    JSONObject jsonRequest = new JSONObject(request);
	    String queryText = jsonRequest.getString("query");
	
	    GraphQLSchema graphQLSchema = graphQLSchemaProvider.getSchema();
	    
	    GraphQL build = GraphQL.newGraphQL(graphQLSchema).build();
	
		ExecutionInput executionInput = ExecutionInput.newExecutionInput()
	    		.query(queryText)
	    		.build();
	    ExecutionResult executionResult = build.execute(executionInput);
	
	    return executionResult.toSpecification();
	}
	
}