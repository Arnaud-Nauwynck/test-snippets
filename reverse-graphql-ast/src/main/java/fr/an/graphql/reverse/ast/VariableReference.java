package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class VariableReference extends Value {

    private String name;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitVariableReference(this, param);
	}
}
