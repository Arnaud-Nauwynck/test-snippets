package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class SchemaDefinition extends AbstractDescribedDefinition {

    private List<Directive> directives;
    private List<OperationTypeDefinition> operationTypeDefinitions;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitSchemaDefinition(this, param);
	}
}
