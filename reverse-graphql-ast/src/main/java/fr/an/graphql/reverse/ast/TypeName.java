package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class TypeName extends Type {

    private String name;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitTypeName(this, param);
	}
}
