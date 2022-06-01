package fr.an.graphql.reverse.ast;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FloatValue extends ScalarValue {

    private BigDecimal value;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitFloatValue(this, param);
	}
}
