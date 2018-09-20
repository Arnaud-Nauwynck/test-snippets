package fr.an.tests.testgraphqlsrv;

import java.io.File;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;

public class JsonQraphQLUtils {

	private static ObjectMapper objectMapper = new ObjectMapper(); 
	
	public static JsonNode parseJsonFile(File file) {
		try {
			return objectMapper.readTree(file);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read json file " + file, ex);
		}
	}
	
	public static JsonNode objectField(JsonNode parent, String fieldName) {
		if (parent == null || !(parent instanceof ObjectNode)) {
			return null;
		}
		ObjectNode parentObj = (ObjectNode) parent;
		return parentObj.get(fieldName);
	}
	
	public static DataFetcher<Object> createObjectFieldDataFetcher() {
		return (env) -> {
			GraphQLFieldDefinition fieldDef = env.getFieldDefinition();
			String fieldName = fieldDef.getName();
			GraphQLType fieldType = fieldDef.getType();
			String typeName = fieldType.getName();
			
			JsonNode obj = (JsonNode) env.getSource();
			JsonNode resNode = objectField(obj, fieldName);
			
			Object res = resNode;
			if (fieldType instanceof GraphQLNonNull) {
				fieldType = ((GraphQLNonNull) fieldType).getWrappedType();
				typeName = fieldType.getName();
			}
			
			//TODO.. handle more type coercions..
			if (fieldType instanceof GraphQLScalarType) {
				if (typeName.equals("Int")) {
					if (resNode instanceof IntNode) {
						res = ((IntNode) resNode).asInt();
					} else {
						res = 0;
					}
				} else if (typeName.equals("String")) {
					if (resNode instanceof TextNode) {
						res = ((TextNode) resNode).asText();
					} else {
						res = "?";
					}
				} else { // unhandled scalar type
					res = resNode;
				}
				
			} else if (fieldType instanceof GraphQLList) {
				ArrayList<JsonNode> resList = new ArrayList<>();
				if (resNode instanceof ArrayNode) {
					ArrayNode arr = (ArrayNode) resNode;
					for(int i = 0, len = arr.size(); i < len; i++) {
						resList.add(arr.get(i));
					}
				} else {
					// ?
				}
				res = resList;
			
			} else if (fieldType instanceof GraphQLObjectType) {
				res = resNode;
			
			} else { // unhandled type
				res = resNode;
			}
			return res;
		};
	}
	
}
