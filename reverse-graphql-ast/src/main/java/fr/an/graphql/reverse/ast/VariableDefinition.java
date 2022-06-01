package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;
import lombok.Value;

@Data
public class VariableDefinition extends AbstractNode {

    private String name;
    private Type type;
    private Value defaultValue;
    private List<Directive> directives;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitVariableDefinition(this, param);
	}
}
