package fr.an.tests.testgraphqlsrv;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

import java.io.File;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.oembedler.moon.graphql.boot.GraphQLJavaToolsAutoConfiguration;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.servlet.DefaultGraphQLSchemaProvider;
import graphql.servlet.GraphQLSchemaProvider;

@Configuration
@AutoConfigureBefore({GraphQLJavaToolsAutoConfiguration.class})
public class GraphQLJsonConfig {


	// cf GraphQLJavaToolsAutoConfiguration
//	@Bean
//	public com.coxautodev.graphql.tools.SchemaParser schemaParser() {
//		// dummy ignore all resolvers..
//		return null; // com.coxautodev.graphql.tools.SchemaParser.newParser().build();
//	}
	
	@Bean
	public GraphQLSchemaProvider graphQLSchemaProvider() {
		SchemaParser schemaParser = new SchemaParser();
		File schemaFile = new File("src/main/resources/test-db1Data.graphqls");
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schemaFile);

		JsonNode db1Data = JsonQraphQLUtils.parseJsonFile(new File("data/db1Data.json"));
		
		DataFetcher<Object> objFieldFetcher = JsonQraphQLUtils.createObjectFieldDataFetcher();
        RuntimeWiring runtimeWiring = newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("db1Data", (env) -> db1Data))
                .type("A", builder -> builder.dataFetcher("id", objFieldFetcher)
                		.dataFetcher("name", objFieldFetcher)
                		.dataFetcher("bs", objFieldFetcher)
                		)
                .type("B", builder -> builder.dataFetcher("id", objFieldFetcher)
                		.dataFetcher("name", objFieldFetcher)
                		)
                .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
		
		return new DefaultGraphQLSchemaProvider(graphQLSchema);
	}
}
