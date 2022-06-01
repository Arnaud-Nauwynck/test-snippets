package fr.an.graphql.reverse.ast;


public class NullValue extends Value {

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitNullValue(this, param);
	}
}
