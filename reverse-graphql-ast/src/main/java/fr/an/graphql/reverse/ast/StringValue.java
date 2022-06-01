package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class StringValue extends ScalarValue {

    private String value;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitStringValue(this, param);
	}
}
