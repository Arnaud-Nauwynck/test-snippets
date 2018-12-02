package fr.an.tests.graphiqlgithub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class GraphiqlGithubApp {

	public static void main(String[] args) {
		SpringApplication.run(GraphiqlGithubApp.class, args);
	}

}

@RestController
class GithubGraphQlProxyController {
	
	public static class GraphQLRequestDTO {
	    public String query;
	    public Map<String, Object> variables = new HashMap<>();
	    public  String operationName;
	}

	public static class GraphQLResultDTO {
		public Object data;
		public List<GraphQLErrorDTO> errors;
		public Map<Object, Object> extensions;
	}

	public static class GraphQLErrorDTO {
	    public String message;
	    public List<SourceLocationDTO> locations;
	    public String errorType;
	}

	public static class SourceLocationDTO {
	    public int line;
	    public int column;
	    public String sourceName;
	}


	private String githubToken;

	CloseableHttpClient client = HttpClients.createDefault();
	ObjectMapper om = new ObjectMapper();

	@Value("${targetGraphQLUrl}")
    String targetGraphQLUrl;

	@Value("${graphQLTokenFile}")
	String graphQLTokenFile;
	
    @PostConstruct
    public void init() throws IOException {
    	System.out.println("reading secret Token from file '" + graphQLTokenFile + "'");
    	githubToken = new String(Files.readAllBytes(Paths.get(graphQLTokenFile)));
    }
    
	@PostMapping("/graphql")
	public GraphQLResultDTO postQueryGithubGraphQL(
			@RequestBody GraphQLRequestDTO query
			) throws Exception {
	    return doQuery(query);
	}

	private GraphQLResultDTO doQuery(GraphQLRequestDTO query) throws Exception {
		HttpPost httpReq = new HttpPost(targetGraphQLUrl);
		httpReq.addHeader("Authorization", "bearer " + githubToken);
	    httpReq.addHeader("Accept", "application/json");
	    httpReq.addHeader("Content-type", "application/json");
	    
	    String queryJson = om.writeValueAsString(query);
	    httpReq.setEntity(new StringEntity(queryJson));
	    
	    // *** delegate to Http POST ***
	    CloseableHttpResponse response = client.execute(httpReq);
	    
	    HttpEntity respEntity = response.getEntity();
	    byte[] respByteArray = EntityUtils.toByteArray(respEntity);
	    GraphQLResultDTO res = om.readValue(respByteArray, GraphQLResultDTO.class);
	    
	    //?? httpReq.completed();
	    return res;
	}
	
}
