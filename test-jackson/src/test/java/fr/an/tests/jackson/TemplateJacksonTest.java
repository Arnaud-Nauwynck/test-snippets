package fr.an.tests.jackson;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class TemplateJacksonTest {

	private static ObjectMapper objectMapper = new ObjectMapper();

	static class A<T> {
		public T data;

		public A(@JsonProperty("data") T data) {
			this.data = data;
		}
		
	}
	
	static class B {
		public String str;
		public B(@JsonProperty("str") String str) {
			this.str = str;
		}
	}

	static class C {
		public String str;
		public C(@JsonProperty("str") String str) {
			this.str = str;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testA() throws Exception {
		String json = "{\"data\":{\"str\":\"test\"}}";
		// JsonNode node = objectMapper.readTree(json);
		
		A<B> a1 = new A<>(new B("test"));
		A<C> a2 = new A<>(new C("test"));
		String a1Json = objectMapper.writeValueAsString(a1);
		String a2Json = objectMapper.writeValueAsString(a2);
		Assert.assertEquals(a1Json, a2Json);
		Assert.assertEquals(json, a1Json);
		
		// test parse A<B> vs A<C>
		JavaType aBType = TypeFactory.defaultInstance().constructParametricType(A.class, B.class);
		A<B> resA1 = (A<B>) objectMapper.readValue(json, aBType);
		Assert.assertTrue(resA1.data instanceof B);
		
		JavaType aCType = TypeFactory.defaultInstance().constructParametricType(A.class, C.class);
		A<?> resA2 = objectMapper.readValue(json, aCType);
		Assert.assertTrue(resA2.data instanceof C);
		
		// test parse A<A<B>> vs A<A<C>>
		JavaType aabType = TypeFactory.defaultInstance().constructParametricType(A.class, aBType);
		JavaType aacType = TypeFactory.defaultInstance().constructParametricType(A.class, aCType);
		String ajson = "{ \"data\": " + json + "}";
		A<?> resAAB = objectMapper.readValue(ajson, aabType);
		A<?> resAAB_data = (A)resAAB.data;
		Assert.assertTrue(resAAB_data.data instanceof B);

		A<?> resAAC = objectMapper.readValue(ajson, aacType);
		A<?> resAAC_data = (A)resAAC.data;
		Assert.assertTrue(resAAC_data.data instanceof C);
	}
	
	
}
