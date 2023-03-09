package fr.iut.tp2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ObjectJsonNode extends JsonNode {

	private final Map<String,JsonNode> child = new LinkedHashMap<>();

	public ObjectJsonNode() {
	}
	
	public ObjectJsonNode(Map<String, JsonNode> src) {
		child.putAll(src);
	}

	public void accept(JsonNodeVisitor v) {
		v.caseObject(this);
	}

	public <T> T accept(JsonNodeVisitor2<T> v) {
		return v.caseObject(this);
	}

	
	public Map<String,JsonNode> getValue() {
		return child;
	}

	public JsonNode get(String key) {
		return child.get(key);
	}

	public JsonNode put(String key, JsonNode value) {
		return child.put(key, value);
	}

	public JsonNode remove(String key) {
		return child.remove(key);
	}
	
	// ------------------------------------------------------------------------

	public static class Builder {
		
		private final Map<String,JsonNode> child = new LinkedHashMap<>();

		public ObjectJsonNode build() {
			return new ObjectJsonNode(child);
		}

		public Builder putNull(String key) {
			child.put(key, new NullJsonNode());
			return this;
		}
		
		public Builder put(String key, JsonNode value) {
			child.put(key, value);
			return this;
		}
		
		public Builder put(String key, boolean b) {
			child.put(key, new BooleanJsonNode(true));
			return this;
		}

		public Builder put(String key, double value) {
			child.put(key, new NumericJsonNode(value));
			return this;
		}

		public Builder put(String key, String value) {
			child.put(key, new TextJsonNode(value));
			return this;
		}

		public Builder putArray(String key, JsonNode... elements) {
			child.put(key, new ArrayJsonNode(elements));
			return this;
		}

		public Builder putArray(String key, Consumer<ArrayJsonNode.Builder> cb) {
			ArrayJsonNode.Builder childBuilder = new ArrayJsonNode.Builder();
			cb.accept(childBuilder);
			ArrayJsonNode value = childBuilder.build();
			child.put(key, value);
			return this;
		}

		public Builder putObj(String key, Consumer<Builder> cb) {
			Builder childBuilder = new Builder();
			cb.accept(childBuilder);
			ObjectJsonNode value = childBuilder.build();
			child.put(key, value);
			return this;
		}
	}

}
