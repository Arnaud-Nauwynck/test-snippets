package fr.an.testgraphql;

import java.util.Map;

import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;

//import v2.io.swagger.models.Operation;
//import v2.io.swagger.models.Path;
//import v2.io.swagger.models.Response;
//import v2.io.swagger.models.Swagger;
//import v2.io.swagger.models.parameters.Parameter;
//import v2.io.swagger.models.properties.ArrayProperty;
//import v2.io.swagger.models.properties.Property;
//import v2.io.swagger.models.refs.RefType;
//import v2.io.swagger.parser.SwaggerParser;

public class SwaggerToGraphQLApp {

	public static void main(String[] args) {
		new SwaggerToGraphQLApp().run();
	}
	
	public void run() {
		  Swagger swagger = new SwaggerParser().read("swagger.json");
		  Map<String, Path> paths = swagger.getPaths();
		  for(Map.Entry<String,Path> pathEntry : paths.entrySet()) {
			  String pathUrl = pathEntry.getKey();
			  Path path = pathEntry.getValue();
			  Operation pathPost = path.getPost();
			  if (pathPost != null) {
				  printOperation("POST:" + pathUrl, pathPost);
			  }
			  Operation pathGet = path.getGet();
			  if (pathGet != null) {
				  printOperation("GET:" + pathUrl, pathGet);
			  }
			  System.out.println();
		  }
	}
	
	public void printOperation(String display, Operation op) {
		System.out.println(display);
		// private List<String> consumes;
	    // private List<String> produces;
	    for(Parameter param: op.getParameters()) {
	    	printParam(param);
	    }
	    // private Map<String, Response> responses;
	    Response okResp = op.getResponses().get("200");
	    if (okResp != null) {
	    	Model okRespModel = okResp.getResponseSchema();
	    	System.out.println(" 200 => ");
	    	if (okRespModel instanceof ArrayModel) {
	    		ArrayModel respModelArray = (ArrayModel) okRespModel;
	    		Property items = respModelArray.getItems();
	    		// items.getType()
	    		if (items instanceof RefProperty) {
	    			RefProperty refProp = (RefProperty) items;
	    			String ref = refProp.getSimpleRef();
	    			System.out.println("  schema: Array " + ref);
	    		}
	    	} else if (okRespModel instanceof RefModel) {
	    		RefModel refModel = (RefModel) okRespModel;
	    		String ref = refModel.getSimpleRef();
    			System.out.println("  schema: Ref " + ref);
	    	}
	    }
	}

	private void printParam(Parameter param) {
		System.out.println(" param:" + param.getName());
	}
}
