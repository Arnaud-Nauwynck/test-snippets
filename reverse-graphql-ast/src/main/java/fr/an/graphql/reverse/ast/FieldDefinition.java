package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class FieldDefinition extends AbstractDescribedNode {

	private String name;
    private Type type;
    private List<InputValueDefinition> inputValueDefinitions;
    private List<Directive> directives;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitFieldDefinition(this, param);
	}
}
