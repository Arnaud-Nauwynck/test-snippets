package fr.iut.tp2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ArrayJsonNode extends JsonNode {

	private final List<JsonNode> child = new ArrayList<>();

	// ------------------------------------------------------------------------
	
	public ArrayJsonNode() {
	}

	public ArrayJsonNode(JsonNode... elements) {
		child.addAll(Arrays.asList(elements));
	}

	public ArrayJsonNode(List<JsonNode> elements) {
		child.addAll(elements);
	}

	// ------------------------------------------------------------------------
	
	public void accept(JsonNodeVisitor v) {
		v.caseArray(this);
	}

	public <T> T accept(JsonNodeVisitor2<T> v) {
		return v.caseArray(this);
	}

	public List<JsonNode> getValue() {
		return child;
	}

	public Iterator<JsonNode> iterator() {
		return child.iterator();
	}


	
	// ------------------------------------------------------------------------
	
	public static class Builder {
		
		private final List<JsonNode> child = new ArrayList<>();

		public ArrayJsonNode build() {
			return new ArrayJsonNode(child);
		}

		public Builder addNull() {
			child.add(new NullJsonNode());
			return this;
		}
		
		public Builder add(JsonNode value) {
			child.add(value);
			return this;
		}
		
		public Builder add(boolean b) {
			child.add(new BooleanJsonNode(true));
			return this;
		}

		public Builder add(double value) {
			child.add(new NumericJsonNode(value));
			return this;
		}

		public Builder add(String value) {
			child.add(new TextJsonNode(value));
			return this;
		}

		public Builder add(JsonNode... elements) {
			child.add(new ArrayJsonNode(elements));
			return this;
		}

		public Builder addArray(Consumer<ArrayJsonNode.Builder> cb) {
			ArrayJsonNode.Builder childBuilder = new ArrayJsonNode.Builder();
			cb.accept(childBuilder);
			ArrayJsonNode value = childBuilder.build();
			child.add(value);
			return this;
		}

		public Builder addObj(Consumer<ObjectJsonNode.Builder> cb) {
			ObjectJsonNode.Builder childBuilder = new ObjectJsonNode.Builder();
			cb.accept(childBuilder);
			ObjectJsonNode value = childBuilder.build();
			child.add(value);
			return this;
		}
	}
	

}
