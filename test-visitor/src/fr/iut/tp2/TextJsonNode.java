package fr.iut.tp2;

public class TextJsonNode extends JsonNode {

	private String value;

	public TextJsonNode() {
	}
	
	public TextJsonNode(String value) {
		this.value = value;
	}

	
	@Override
	public void accept(JsonNodeVisitor v) {
		v.caseText(this);
	}

	@Override
	public <T> T accept(JsonNodeVisitor2<T> v) {
		return v.caseText(this);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
