package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class OperationTypeDefinition extends AbstractNode {

    private String name;
    private TypeName typeName;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitOperationTypeDefinition(this, param);
	}
}
