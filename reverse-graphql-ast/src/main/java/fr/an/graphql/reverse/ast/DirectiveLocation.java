package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class DirectiveLocation extends AbstractNode {

	private final String name;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitDirectiveLocation(this, param);
	}

}
