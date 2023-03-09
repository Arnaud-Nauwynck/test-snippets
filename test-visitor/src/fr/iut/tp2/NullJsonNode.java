package fr.iut.tp2;

public class NullJsonNode extends JsonNode {

	public void accept(JsonNodeVisitor v) {
		v.caseNull(this);
	}

	public <T> T accept(JsonNodeVisitor2<T> v) {
		return v.caseNull(this);
	}


}
