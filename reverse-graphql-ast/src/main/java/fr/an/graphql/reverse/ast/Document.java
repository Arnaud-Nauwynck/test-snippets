package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class Document extends AbstractNode {

    private List<Definition> definitions;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitDocument(this, param);
	}

}
