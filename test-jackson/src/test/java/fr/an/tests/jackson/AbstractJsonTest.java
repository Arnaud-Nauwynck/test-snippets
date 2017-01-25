package fr.an.tests.jackson;

import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractJsonTest {

	
	private static ObjectMapper DEFAULT_objectMapper = new ObjectMapper();
	private static ObjectMapper ACCEPT_UNKOWN_PROPS_objectMapper = new ObjectMapper();
	static {
		ACCEPT_UNKOWN_PROPS_objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
	}

	public static <T> T serializeToJsonThenToObj(T obj) {
		@SuppressWarnings("unchecked")
		Class<T> clss = (Class<T>) obj.getClass();
		return serializeToJsonThenToObj(obj, clss);
	}

	public static <T> T serializeToJsonThenToObj(Object obj, Class<T> clss) {
		String jsonText = toJson(obj);
		T res = fromJson(jsonText, clss);
		return res;
	}

	public static String toJson(Object obj) {
		return toJson(DEFAULT_objectMapper, obj);
	}

	public static <T> T fromJson(String json, Class<T> clss) {
		return fromJson(DEFAULT_objectMapper, json, clss);
	}

	public static <T> T fromJson_AcceptUnknownProps(String json, Class<T> clss) {
		return fromJson(ACCEPT_UNKOWN_PROPS_objectMapper, json, clss);
	}
	

	public static String toJson(ObjectMapper objectMapper, Object obj) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			objectMapper.writeValue(buffer, obj);
		} catch (Exception ex) {
			throw new RuntimeException("Failed obj->json", ex);
		}
		String jsonText = buffer.toString();
		return jsonText;
	}
	public static <T> T fromJson(ObjectMapper objectMapper, String json, Class<T> clss) {
		try {
			T res = objectMapper.readValue(json, clss);
			return res;
		} catch (Exception ex) {
			throw new RuntimeException("Failed json->obj", ex);
		}
	}


}
