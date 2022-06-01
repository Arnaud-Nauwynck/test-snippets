package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class DirectiveDefinition extends AbstractDescribedDefinition {

	private String name;
    private boolean repeatable;
    private List<InputValueDefinition> inputValueDefinitions;
    private List<DirectiveLocation> directiveLocations;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitDirectiveDefinition(this, param);
	}

}
