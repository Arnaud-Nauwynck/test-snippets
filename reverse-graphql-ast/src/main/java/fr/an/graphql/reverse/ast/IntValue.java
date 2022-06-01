package fr.an.graphql.reverse.ast;

import java.math.BigInteger;

import lombok.Data;

@Data
public class IntValue extends ScalarValue {

    private BigInteger value;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitIntValue(this, param);
	}
}
