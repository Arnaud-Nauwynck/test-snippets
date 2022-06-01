package fr.an.tests.testgraphqljava;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import fr.an.tests.testgraphqljava.domain.A;
import fr.an.tests.testgraphqljava.domain.B;
import fr.an.tests.testgraphqljava.repository.RootQuery;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.val;

public class GraphQLTest {

	@Test
	public void testExecute() {
		File schemaFile = new File("src/main/resources/test-graphql-java.graphqls");
		TypeDefinitionRegistry typeDefinitionRegistry = new SchemaParser().parse(schemaFile);

		RuntimeWiring runtimeWiring = createRuntimeWiring();
		
		GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
		
		GraphQL graphQL = new GraphQL.Builder(schema)
				.build();

		RootQuery rootQuery = new RootQuery();
		
		String query = "{ aById(id: 1) { name } }";
		// => Document.definitions:
		// [OperationDefinition{name='null', operation=QUERY, variableDefinitions=[], directives=[], 
		//   selectionSet=SelectionSet{
		//     selections=[Field{name='aById', alias='null', arguments=[Argument{name='id', value=IntValue{value=1}}], directives=[], selectionSet=SelectionSet{
		//       selections=[Field{name='name', alias='null', arguments=[], directives=[], selectionSet=null}]}
		//           }]
		//   }}]

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .root(rootQuery)
                .build();
        ExecutionResult res = graphQL.execute(executionInput);
        Object resData = res.getData();
        Assert.assertNotNull(resData);
        
        val data = (Map<String,Object>) resData;
        val aData = (Map<String,Object>) data.get("aById");
        val aName = (String) aData.get("name");
        Assert.assertEquals("a1", aName);
        
        // redo execute same query => reparse..
        ExecutionInput executionInput2 = ExecutionInput.newExecutionInput()
                .query(query)
                .root(rootQuery)
                .build();
        ExecutionResult res2 = graphQL.execute(executionInput2);
	}

	protected RuntimeWiring createRuntimeWiring() {
		RuntimeWiring.Builder rtBuilder = RuntimeWiring.newRuntimeWiring();
		// rtBuilder.

		rtBuilder.type("Query", b -> {
			b.dataFetcher("aById", env -> {
				RootQuery obj = (RootQuery) env.getRoot();
				int id = (Integer) env.getArguments().get("id");
				return obj.findAById(id);
			});
			b.dataFetcher("a", env -> {
				RootQuery obj = (RootQuery) env.getRoot();
				return obj.findAllA();
			});
			return b;
		});

		rtBuilder.type("A", b -> {
			b.dataFetcher("id", env -> {
				A obj = (A) env.getSource();
				return obj.id;
			});
			b.dataFetcher("name", env -> {
				A obj = (A) env.getSource();
				return obj.name;
			});
			b.dataFetcher("bs", env -> {
				A obj = (A) env.getSource();
				return obj.bs;
			});
			return b;
		});
		
		rtBuilder.type("B", builder -> {
			builder.dataFetcher("id", env -> {
				B obj = (B) env.getSource();
				return obj.id;
			});
			builder.dataFetcher("name", env -> {
				B obj = (B) env.getSource();
				return obj.getName();
			});
			builder.dataFetcher("a", env -> {
				B obj = (B) env.getSource();
				return obj.getA();
			});
			return builder;
		});

		RuntimeWiring runtimeWiring = rtBuilder.build();
		return runtimeWiring;
	}
}
