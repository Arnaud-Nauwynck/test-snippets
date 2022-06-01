package fr.an.graphql.reverse.ast;

import java.util.List;

import lombok.Data;

@Data
public class OperationDefinition extends Definition {

    public enum Operation {
        QUERY, MUTATION, SUBSCRIPTION
    }

    private String name;

    private Operation operation;
    private List<VariableDefinition> variableDefinitions;
    private List<Directive> directives;
    private SelectionSet selectionSet;

	@Override
    public <TParam,TRes> TRes accept(NodeVisitor<TParam,TRes> visitor, TParam param) {
		return visitor.visitOperationDefinition(this, param);
	}
}
