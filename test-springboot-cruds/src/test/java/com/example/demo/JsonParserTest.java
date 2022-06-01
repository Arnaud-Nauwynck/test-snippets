package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonParserTest {

	@Test
	public void testParseJson() throws Exception {
		String json = "[ 123, true, { \"f\": \"abcd\" } ]";
		
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createParser(json);
		
		JsonToken tk;
		tk = parser.nextToken(); assertEquals(JsonToken.START_ARRAY, tk);
		tk = parser.nextToken(); assertEquals(JsonToken.VALUE_NUMBER_INT, tk);
		int i = parser.getIntValue(); assertEquals(123, i);

		tk = parser.nextToken(); assertEquals(JsonToken.VALUE_TRUE, tk);

		tk = parser.nextToken(); assertEquals(JsonToken.START_OBJECT, tk);
		tk = parser.nextToken(); assertEquals(JsonToken.FIELD_NAME, tk);
		String name = parser.getCurrentName(); assertEquals("f", name);
		tk = parser.nextToken(); assertEquals(JsonToken.VALUE_STRING, tk);
		String text = parser.getText(); assertEquals("abcd", text);
		tk = parser.nextToken(); assertEquals(JsonToken.END_OBJECT, tk);
		
		tk = parser.nextToken(); assertEquals(JsonToken.END_ARRAY, tk);
		tk = parser.nextToken(); assertNull(tk);
	}
	
	public static class User {
		public String name;
	}
	
	public static class DetailedUser {
		public String name;
		public String address; // => null  if unset
		public int age;        // => 0     if unset
		public boolean adult;  // => false if unset
	}
	
	@Test
	public void testJson2Object() throws Exception {
		
		String json = "{ \"name\": \"abcd\" }";
		
		ObjectMapper om = new ObjectMapper();
		User bean = om.readValue(json, User.class);
		
		assertEquals("abcd", bean.name);
	
	}

	@Test
	public void testJson2Object_unknownField_strict() throws Exception {
		
		String json = "{ \"name\": \"abcd\", \"xxx\": 123 }";
		
		ObjectMapper om = new ObjectMapper();
		// om.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // default
		try {
			om.readValue(json, User.class);
			Assert.fail(); // 
		} catch(UnrecognizedPropertyException ex) {
			// ok !
		}
	
	}

	
	@Test
	public void testJson2Object_unknownField() throws Exception {
		
		String json = "{ \"name\": \"abcd\", \"xxx\": 123 }";
		
		ObjectMapper om = new ObjectMapper();
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // override
		User bean = om.readValue(json, User.class);
		
		assertEquals("abcd", bean.name);
		// field "xxx" not mapped to bean
	}

	
	
	@Test
	public void testJson2Tree() throws Exception {
		
		String json = "[123, { \"name\": \"abcd\" }]";
		
		ObjectMapper om = new ObjectMapper();
		JsonNode tree = om.readTree(json);
		
		assertEquals(JsonToken.START_ARRAY, tree.asToken());
		JsonNode elt0 = tree.get(0), elt1 = tree.get(1);
		assertEquals(123, elt0.asInt());
		assertEquals(JsonToken.START_OBJECT, elt1.asToken());
		JsonNode nameNode = elt1.findValue("name");
		assertEquals(JsonToken.VALUE_STRING, nameNode.asToken());
		assertEquals("abcd", nameNode.asText());
		
	}

	@Test
	public void testTree2Json() throws Exception {
		
		JsonNodeFactory f = new JsonNodeFactory(true);
		ArrayNode arrayNode = f.arrayNode();
		arrayNode.add(f.numberNode(123));
		ObjectNode objectNode = f.objectNode();
		objectNode.set("name", f.textNode("abcd"));
		arrayNode.add(objectNode);
		
		String json = arrayNode.toString();
		// arrayNode.toPrettyString();
			
		assertEquals("[123,{\"name\":\"abcd\"}]", json);
		
	}
	
	
	@Test
	public void testObject2Json() throws Exception {
		User bean = new User();
		bean.name = "abcd";
		
		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(bean);
		
		assertEquals("{\"name\":\"abcd\"}", json);
	}

	@JsonTypeInfo(use=Id.NAME, property="type")
	@JsonSubTypes({
		@Type(name="dog", value=Dog.class),
		@Type(name="duck", value=Duck.class),
	})
	public static abstract class Animal {
		
	}

	public static class Dog extends Animal {
		public String barking;
	}

	public static class Duck extends Animal {
		public String quacking;
	}

	class UserDefinedDTO {
		
		public String field1;
		public String field2;
		
		public String field5;
		public String field6;

		
		// private, no getter => ignored
		private String ignoreField7;
		@JsonIgnore // explicitly ignored
		public String ignoreField8;
	}
	
	@Test
	public void testSubObject2Json() throws Exception {
		Dog dog = new Dog();
		dog.barking = "houah";
		Animal animal = dog;
		
		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(animal);
		
		assertEquals("{\"type\":\"dog\",\"barking\":\"houah\"}", json);
		
		Animal a = om.readValue(json, Animal.class); // parse any from abstract class
		assertTrue(a instanceof Dog); // instanciated sub-class
		assertEquals("houah", ((Dog) a).barking);
	}
	
	
	public static class UntypedDataDTO {
		public String field1;
		public JsonNode untypedData;
	}
	
	@Test
	public void testUntypedDataDTO2Json() throws Exception {
		
		UntypedDataDTO bean = new UntypedDataDTO();
		bean.field1 = "abcd";
		JsonNodeFactory f = new JsonNodeFactory(true);
		ArrayNode arrayNode = f.arrayNode();
		arrayNode.add(f.numberNode(123));
		arrayNode.add(f.textNode("abc"));
		bean.untypedData = arrayNode;
		
		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(bean);
		
		assertEquals("{\"field1\":\"abcd\",\"untypedData\":[123,\"abc\"]}", json);
		
	}
	
	@Test
	public void testJson2UntypedDataDTO() throws Exception {
		
		String json = "{\"field1\":\"abcd\",\"untypedData\":[123,\"abc\"]}";
		
		ObjectMapper om = new ObjectMapper();
		UntypedDataDTO bean = om.readValue(json, UntypedDataDTO.class);
		
		assertEquals("abcd", bean.field1);
		ArrayNode arrayNode = (ArrayNode) bean.untypedData;
		assertEquals(2, arrayNode.size());
		JsonNode elt0 = arrayNode.get(0), elt1 = arrayNode.get(1);
		assertEquals(123, elt0.asInt());
		assertEquals("abc", elt1.asText());
	
	}

	public static class ExtraDataDTO {
		public String field1;
		
		private Map<String,Object> extraData = new LinkedHashMap<>();
		public Object getExtraData(String key) {
			return extraData.get(key);
		}
		
		@JsonAnyGetter
		public Map<String,Object> getExtraData() {
			return extraData;
		}
		@JsonAnySetter
		public void setExtraData(String key, Object value) {
			this.extraData.put(key, value);
		}
	}
	
	@Test
	public void testExtraDataDTO2Json() throws Exception {
		
		ExtraDataDTO bean = new ExtraDataDTO();
		bean.field1 = "abcd";
		bean.setExtraData("field2", "bcde");
		bean.setExtraData("field3", 123);
		
		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(bean);
		
		String expectedJson = "{" + 
				"\"field1\":\"abcd\"," + 
				"\"field2\":\"bcde\"," + 
				"\"field3\":123" + 
				"}";
		assertEquals(expectedJson, json);
		
	}
	
	@Test
	public void testJson2ExtraDTO() throws Exception {
		
		String json = "{" + 
				"\"field1\":\"abcd\"," + 
				"\"field2\":\"bcde\"," + 
				"\"field3\":123" + 
				"}";
		
		ObjectMapper om = new ObjectMapper();
		ExtraDataDTO bean = om.readValue(json, ExtraDataDTO.class);
		
		assertEquals("abcd", bean.field1);
		Object value2 = bean.getExtraData("field2");
		Object value3 = bean.getExtraData("field3");
		assertEquals(2, bean.getExtraData().size());
		assertEquals(123, ((Integer) value2).intValue());
		assertEquals("abc", (String) value3);
	
	}
	
	
}
