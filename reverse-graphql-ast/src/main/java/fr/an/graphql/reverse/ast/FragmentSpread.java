package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class FragmentSpread extends Selection {

    private String name;
    private List<Directive> directives;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitFragmentSpread(this, param);
	}
}
