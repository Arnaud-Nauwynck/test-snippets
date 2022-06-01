package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class ArrayValue extends Value {

    private List<Value> values;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitArrayValue(this, param);
	}

}
