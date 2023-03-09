package fr.iut.tp2;

public class BooleanJsonNode extends JsonNode {

	private boolean value;

	public BooleanJsonNode() {
	}
	
	public BooleanJsonNode(boolean p) {
		this.value = p;
	}

	
	public void accept(JsonNodeVisitor v) {
		v.caseBoolean(this);
	}

	public <T> T accept(JsonNodeVisitor2<T> v) {
		return v.caseBoolean(this);
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}
	
}
