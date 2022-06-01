package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class ObjectField extends AbstractNode {

    private String name;
    private Value value;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitObjectField(this, param);
	}
}
