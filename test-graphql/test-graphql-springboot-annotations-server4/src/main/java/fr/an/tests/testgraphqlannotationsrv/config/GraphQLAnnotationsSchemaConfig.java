package fr.an.tests.testgraphqlannotationsrv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import fr.an.tests.testgraphqlannotationsrv.resolver.RootQuery;
import graphql.GraphQL;
import graphql.annotations.AnnotationsSchemaCreator;
import graphql.annotations.AnnotationsSchemaCreator.Builder;
import graphql.schema.GraphQLSchema;
import graphql.spring.web.servlet.GraphQLRootProvider;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class GraphQLAnnotationsSchemaConfig {

	@Bean
	public GraphQLSchema schema() {
		Builder schemaBuilder = AnnotationsSchemaCreator.newAnnotationsSchema();
		schemaBuilder
		        .query(RootQuery.class) // to create you query object
//		        .mutation(Mutation.class) // to create your mutation object
//		        .subscription(Subscription.class) // to create your subscription object
//		        .directive(UpperDirective.class) // to create a directive
//		        .additionalType(AdditionalType.class) // to create some additional type and add it to the schema
//		        .typeFunction(CustomType.class) // to add a typefunction
//		        .setAlwaysPrettify(true) // to set the global prettifier of field names (removes get/set/is prefixes from names)
//		        .setRelay(customRelay) // to add a custom relay object
		        ;  
		
		// cf also grapql-spring-annotations:
		// https://github.com/yarinvak/graphql-spring-annotations/blob/master/src/main/java/springAnno/schema/SchemaConfiguration.java#L59
		
		GraphQLSchema schema = schemaBuilder.build(); 

		val allTypes = schema.getAllTypesAsList();
		val sb = new StringBuilder();
		sb.append("detected GraphQL types:\n");
		for(val type : allTypes) {
			val descr = type.getDescription();
			if (descr != null && descr.startsWith("Built-in")) {
				continue;
			}
			sb.append(type + "\n");
		}
		log.info(sb.toString());
		
		return schema;
	}
	
	@Component
	public static class MyGraphQLRootProvider implements GraphQLRootProvider {
		@Autowired 
		RootQuery rootQuery;
		
		@Override
		public Object get() {
			return rootQuery;
		}
		
	}
	
	@Bean
	public GraphQL graphQL(GraphQLSchema schema) {
		val builder = GraphQL.newGraphQL(schema);
		return builder.build();
	}
	
}
