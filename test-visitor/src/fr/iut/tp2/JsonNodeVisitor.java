package fr.iut.tp2;

public interface JsonNodeVisitor {

	public abstract void caseNull(NullJsonNode p);
	public abstract void caseBoolean(BooleanJsonNode p);
	public abstract void caseNumeric(NumericJsonNode p);
	public abstract void caseText(TextJsonNode p);
	public abstract void caseArray(ArrayJsonNode p);
	public abstract void caseObject(ObjectJsonNode p);
	
}
