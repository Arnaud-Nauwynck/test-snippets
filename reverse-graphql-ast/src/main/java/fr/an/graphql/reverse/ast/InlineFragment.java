package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class InlineFragment extends Selection {

	private TypeName typeCondition;
    private List<Directive> directives;
    private SelectionSet selectionSet;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitInlineFragment(this, param);
	}
}
