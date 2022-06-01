package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class FragmentDefinition extends Definition {

    private String name;
    private TypeName typeCondition;
    private List<Directive> directives;
    private SelectionSet selectionSet;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitFragmentDefinition(this, param);
	}
}
