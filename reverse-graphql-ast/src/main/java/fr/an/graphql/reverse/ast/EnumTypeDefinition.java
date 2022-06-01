package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class EnumTypeDefinition extends AbstractDescribedDefinition {

	private String name;
    private List<EnumValueDefinition> enumValueDefinitions;
    private List<Directive> directives;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitEnumTypeDefinition(this, param);
	}
}
