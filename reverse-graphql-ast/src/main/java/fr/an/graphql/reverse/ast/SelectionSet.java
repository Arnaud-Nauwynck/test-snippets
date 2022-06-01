package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class SelectionSet extends AbstractNode {

    private List<Selection> selections;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitSelectionSet(this, param);
	}
}
