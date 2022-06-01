package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class ObjectTypeDefinition extends AbstractDescribedDefinition {

	private String name;
    private List<Type> implementz;
    private List<Directive> directives;
    private List<FieldDefinition> fieldDefinitions;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitObjectTypeDefinition(this, param);
	}
}
