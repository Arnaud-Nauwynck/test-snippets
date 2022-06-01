package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class BooleanValue extends ScalarValue {

    private final boolean value;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitBooleanValue(this, param);
	}

}
