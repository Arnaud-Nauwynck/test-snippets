package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class Directive extends AbstractNode {

	private String name;
    private List<Argument> arguments;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitDirective(this, param);
	}

}
