package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class InterfaceTypeDefinition extends AbstractDescribedDefinition {

    private String name;
    private List<Type> implementz;
    private List<FieldDefinition> definitions;
    private List<Directive> directives;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitInterfaceTypeDefinition(this, param);
	}
}
