package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class Field extends Selection {

    private String name;
    private String alias;
    private List<Argument> arguments;
    private List<Directive> directives;
    private SelectionSet selectionSet;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitField(this, param);
	}
}
