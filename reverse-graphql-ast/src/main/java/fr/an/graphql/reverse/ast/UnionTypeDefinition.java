package fr.an.graphql.reverse.ast;

import java.lang.ProcessBuilder.Redirect.Type;
import java.util.List;

import lombok.Data;

@Data
public class UnionTypeDefinition extends AbstractDescribedDefinition {

    private String name;
    private List<Directive> directives;
    private List<Type> memberTypes;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitUnionTypeDefinition(this, param);
	}
}
