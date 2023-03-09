package fr.iut.tp2;

public interface JsonNodeVisitor2<T> {

	public abstract T caseNull(NullJsonNode p);
	public abstract T caseBoolean(BooleanJsonNode p);
	public abstract T caseNumeric(NumericJsonNode p);
	public abstract T caseText(TextJsonNode p);
	public abstract T caseArray(ArrayJsonNode p);
	public abstract T caseObject(ObjectJsonNode p);
	
}
