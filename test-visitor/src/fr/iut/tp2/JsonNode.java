package fr.iut.tp2;

public abstract class JsonNode {

	public abstract void accept(JsonNodeVisitor v);

	public abstract <T> T accept(JsonNodeVisitor2<T> v);
	
	
	public String toJson() {
		PrintJsonVisitor v = new PrintJsonVisitor();
		return this.accept(v);
	}

}
