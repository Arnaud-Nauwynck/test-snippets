package fr.an.tests.testgraphqlsrv.impl;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.ExecutionInput;
import graphql.introspection.IntrospectionQuery;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class HttpGraphQLRemoteRetriever implements GraphQLRemoteRetriever<Object> {

    private String url;
    private CloseableHttpClient httpClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    public HttpGraphQLRemoteRetriever(String url) {
        this.url = url;
        this.httpClient = HttpClientBuilder.create()
        		.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(30000).build())
        		.build();
    }

	public CompletableFuture<Map<String, Object>> queryGraphQL(ExecutionInput executionInput, Object object) {
    	String graphqlQuery = executionInput.toString();
    	Map<String, Object> result = postGraphQLQuery(graphqlQuery);
        return CompletableFuture.completedFuture(result);
    }
	
	public Map<String, Object> postGraphQLQuery(String graphqlQuery) {
		Map<String, Object> result;
        HttpPost httpPost = new HttpPost(url.toString());
		HttpEntity httpEntity = new ByteArrayEntity(graphqlQuery.getBytes());
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.execute(httpPost);
            
            InputStream responseStream = httpResponse.getEntity().getContent();
            @SuppressWarnings("unchecked")
			Map<String,Object> tmpres = objectMapper.readValue(responseStream, Map.class);
			result = tmpres;
			responseStream.close();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to execute qraphql query request to " + url, ex);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception ex) {
                    // failed to close, ignore
                }
            }
        }
		return result;
	}


	public TypeDefinitionRegistry getIntrospectionTypeDefinitionRegistry() {
		Document document = getIntrospectionSchemaDocument();
		TypeDefinitionRegistry res = new SchemaParser().buildRegistry(document);
		return res;
	}
	
	public Document getIntrospectionSchemaDocument() {
//		String jsonReq = "{ \"query\": \"" + IntrospectionQuery.INTROSPECTION_QUERY + "\" }";
//		Map<String, Object> introspectionResult = postGraphQLQuery(jsonReq);
		// equivalent to GET "/graphql/schema.json"
		Map<String, Object> introspectionResult = getGraphQLSchemaJson();
		
		Document res = new IntrospectionResultToSchema().createSchemaDefinition(introspectionResult);
		return res;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getGraphQLSchemaJson() {
		Map<String, Object> result;
        HttpGet httpPost = new HttpGet(url.toString() + "/schema.json");
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.execute(httpPost);
            
            InputStream responseStream = httpResponse.getEntity().getContent();
            Map<String,Object> tmpres = objectMapper.readValue(responseStream, Map.class);
			result = (Map<String,Object>) tmpres.get("data");
			responseStream.close();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to execute qraphql query request to " + url, ex);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception ex) {
                    // failed to close, ignore
                }
            }
        }
		return result;
	}
	
}
