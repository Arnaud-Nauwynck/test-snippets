package fr.an.tests.testgraphqlsrv;

import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.atlassian.braid.Braid;
import com.atlassian.braid.Braid.BraidGraphQL;
import com.atlassian.braid.SchemaNamespace;
import com.atlassian.braid.source.GraphQLRemoteSchemaSource;

import fr.an.tests.testgraphqlsrv.impl.HttpGraphQLRemoteRetriever;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.preparsed.PreparsedDocumentProvider;
import graphql.language.Document;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.SchemaPrinter;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.servlet.AbstractGraphQLHttpServlet;
import graphql.servlet.DefaultGraphQLSchemaProvider;
import graphql.servlet.ExecutionStrategyProvider;
import graphql.servlet.GraphQLInvocationInputFactory;
import graphql.servlet.GraphQLObjectMapper;
import graphql.servlet.GraphQLQueryInvoker;
import graphql.servlet.GraphQLSchemaProvider;
import graphql.servlet.GraphQLSingleInvocationInput;

@SpringBootApplication
public class GraphqlBraidStitchApp {

	public static void main(String[] args) {
        SpringApplication.run(GraphqlBraidStitchApp.class, args);
    }

	@Bean
	public Braid braidGraphQL() {
		HttpGraphQLRemoteRetriever httpSrv1 = new HttpGraphQLRemoteRetriever("http://localhost:8090/graphql");
		Document schemaDoc1 = httpSrv1.getIntrospectionSchemaDocument();
		String schemaText1 = new SchemaPrinter().print(schemaDoc1);
		//TypeDefinitionRegistry typeSrv1 = httpSrv1.getIntrospectionTypeDefinitionRegistry();
		GraphQLRemoteSchemaSource<Object> schema1 = GraphQLRemoteSchemaSource.builder() //
				.namespace(SchemaNamespace.of("srv1"))
				.remoteRetriever(httpSrv1) //
				.schemaProvider(() -> new StringReader(schemaText1)) // otherwise NPE ..
				.build();

		HttpGraphQLRemoteRetriever httpSrv2 = new HttpGraphQLRemoteRetriever("http://localhost:8092/graphql");
		Document schemaDoc2 = httpSrv2.getIntrospectionSchemaDocument();
		String schemaText2 = new SchemaPrinter().print(schemaDoc2);
		// TypeDefinitionRegistry typeSrv2 = httpSrv1.getIntrospectionTypeDefinitionRegistry();
		GraphQLRemoteSchemaSource<Object> schema2 = GraphQLRemoteSchemaSource.builder() //
				.namespace(SchemaNamespace.of("srv2"))
				.remoteRetriever(httpSrv2) //
				.schemaProvider(() -> new StringReader(schemaText2)) // otherwise NPE ..
				.build();
		
		Braid braid = Braid.builder()
				.schemaSource(schema1)
				.schemaSource(schema2)
				.build();
		
		// Then to execute... per request:
//		BraidGraphQL braidGraphql = braid.newGraphQL();
//		
//		Object myContext = new Object();
//		CompletableFuture<ExecutionResult> result = braidGraphql.execute(newExecutionInput().query(...).context(myContext).build());
//	
//		=> 
//		  DataLoaderRegistry dlr;
//        Function<DataLoaderRegistry, GraphQL> graphQLFactory;
//		
//        final GraphQL graphQL = this.graphQLFactory.apply(dlr);
//        final ExecutionInput newInput = executionInput
//                .transform(builder -> builder.context(new MutableBraidContext<>(dlr, executionInput.getContext())));
//        return graphQL.executeAsync(newInput);
//
		
		return braid;
	}
	
	@Bean
	public GraphQLSchemaProvider graphQLSchemaProvider(Braid braid) {
		GraphQLSchema schema = braid.getSchema();
		return new DefaultGraphQLSchemaProvider(schema);
	}
	
	// cf com.oembedler.moon.graphql.boot.GraphQLWebAutoConfiguration.invocationInputFactory(GraphQLSchemaProvider)
	@Bean
	public GraphQLInvocationInputFactory invocationInputFactory(Braid braid) {
		GraphQLSchema schema = braid.getSchema();
		return GraphQLInvocationInputFactory.newBuilder(schema)
				// .withGraphQLContextBuilder(contextBuilder)
				// .withGraphQLRootObjectBuilder(rootObjectBuilder)
				.build();
	}
	

    @Bean
    public GraphQLQueryInvoker queryInvoker(
    		Supplier<ExecutionStrategyProvider> getExecutionStrategyProvider,
			Supplier<Instrumentation> getInstrumentation,
			Supplier<PreparsedDocumentProvider> getPreparsedDocumentProvider,
    		Braid braidGraphQL) {
    	return new BraidGraphQLQueryInvoker(getExecutionStrategyProvider, getInstrumentation, getPreparsedDocumentProvider, 
    			braidGraphQL);
    }
    
    protected static class BraidGraphQLQueryInvoker extends GraphQLQueryInvoker {
    	Braid braidGraphQL;
		public BraidGraphQLQueryInvoker(
				Supplier<ExecutionStrategyProvider> getExecutionStrategyProvider,
				Supplier<Instrumentation> getInstrumentation,
				Supplier<PreparsedDocumentProvider> getPreparsedDocumentProvider,
				Braid braidGraphQL) {
			super(getExecutionStrategyProvider, getInstrumentation, getPreparsedDocumentProvider);
			this.braidGraphQL = braidGraphQL;
		}

		 public ExecutionResult query(GraphQLSingleInvocationInput singleInvocationInput) {
			 BraidGraphQL braidGraphql = braidGraphQL.newGraphQL();
			 ExecutionInput executionInput = singleInvocationInput.getExecutionInput();
			 
			 CompletableFuture<ExecutionResult> result = braidGraphql.execute(executionInput);
			 
			 try {
				return result.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException("Failed", e);
			}
		 }
    }
    
    
    
	// cf servlet SimpleGraphQLServlet .. from 
	// com.oembedler.moon.graphql.boot.GraphQLWebAutoConfiguration.graphQLHttpServlet(GraphQLInvocationInputFactory, GraphQLQueryInvoker, GraphQLObjectMapper)
    @Bean
    // @ConditionalOnMissingBean
    public AbstractGraphQLHttpServlet graphQLHttpServlet(
    		GraphQLInvocationInputFactory invocationInputFactory, 
    		GraphQLQueryInvoker queryInvoker, 
    		GraphQLObjectMapper graphQLObjectMapper) {
//        return SimpleGraphQLHttpServlet.newBuilder(invocationInputFactory)
//                .withQueryInvoker(queryInvoker)
//                .withObjectMapper(graphQLObjectMapper)
//                // TODO ? .withListeners(listeners)
//                .build();
    	return new BraidGraphQLHttpServlet(invocationInputFactory, 
        		queryInvoker, 
        		graphQLObjectMapper);
    }

    
    protected static class BraidGraphQLHttpServlet extends AbstractGraphQLHttpServlet {

    	GraphQLInvocationInputFactory invocationInputFactory; 
		GraphQLQueryInvoker queryInvoker;
		GraphQLObjectMapper graphQLObjectMapper;
		

        public BraidGraphQLHttpServlet(GraphQLInvocationInputFactory invocationInputFactory,
				GraphQLQueryInvoker queryInvoker, GraphQLObjectMapper graphQLObjectMapper) {
			super();
			this.invocationInputFactory = invocationInputFactory;
			this.queryInvoker = queryInvoker;
			this.graphQLObjectMapper = graphQLObjectMapper;
		}

		@Override
        protected GraphQLQueryInvoker getQueryInvoker() {
            return queryInvoker;
        }

        @Override
        protected GraphQLInvocationInputFactory getInvocationInputFactory() {
            return invocationInputFactory;
        }

        @Override
        protected GraphQLObjectMapper getGraphQLObjectMapper() {
            return graphQLObjectMapper;
        }

		@Override
		public String executeQuery(String query) {
			return super.executeQuery(query);
		}
        
    	
    }
    
}
