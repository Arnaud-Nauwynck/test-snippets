package fr.an.graphql.reverse.ast;

import java.util.List;

public class EnumValueDefinition extends AbstractDescribedNode {

	private String name;
    private List<Directive> directives;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitEnumValueDefinition(this, param);
	}
}
