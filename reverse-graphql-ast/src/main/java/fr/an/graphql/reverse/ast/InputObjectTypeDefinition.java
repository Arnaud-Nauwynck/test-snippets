package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class InputObjectTypeDefinition extends AbstractDescribedDefinition {

    private String name;
    private List<Directive> directives;
    private List<InputValueDefinition> inputValueDefinitions;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitInputObjectTypeDefinition(this, param);
	}
}
