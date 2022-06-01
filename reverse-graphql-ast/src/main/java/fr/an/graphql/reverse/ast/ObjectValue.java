package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class ObjectValue extends Value {

    private List<ObjectField> objectFields;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitObjectValue(this, param);
	}
}
