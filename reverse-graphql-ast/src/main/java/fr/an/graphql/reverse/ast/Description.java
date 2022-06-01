package fr.an.graphql.reverse.ast;

import lombok.Data;

@Data
public class Description {

	public String content;
    public SourceLocation sourceLocation;
    public boolean multiLine;

//	@Override
//    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
//		return visitor.visitDescription(this, param);
//	}

}
