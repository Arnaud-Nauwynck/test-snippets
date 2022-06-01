package fr.an.graphql.reverse.ast;

import lombok.Data;

public class SchemaExtensionDefinition extends SchemaDefinition {

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitSchemaDefinition(this, param);
	}
}
