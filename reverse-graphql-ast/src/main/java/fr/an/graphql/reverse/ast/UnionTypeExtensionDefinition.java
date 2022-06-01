package fr.an.graphql.reverse.ast;

public class UnionTypeExtensionDefinition extends UnionTypeDefinition {

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitUnionTypeDefinition(this, param);
	}
}
