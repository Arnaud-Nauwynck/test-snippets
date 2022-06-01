package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class EnumValue extends Value {

    private String name;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitEnumValue(this, param);
	}
}
