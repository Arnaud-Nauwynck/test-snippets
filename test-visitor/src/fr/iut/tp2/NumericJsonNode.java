package fr.iut.tp2;

public class NumericJsonNode extends JsonNode {

	private Number value;

	
	public NumericJsonNode() {
	}

	public NumericJsonNode(Number value) {
		this.value = value;
	}

	public void accept(JsonNodeVisitor v) {
		v.caseNumeric(this);
	}

	public <T> T accept(JsonNodeVisitor2<T> v) {
		return v.caseNumeric(this);
	}
	
	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

}
