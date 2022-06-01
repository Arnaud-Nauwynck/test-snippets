package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class NonNullType extends Type {

    private Type type;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitNonNullType(this, param);
	}
}
