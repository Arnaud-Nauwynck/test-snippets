package fr.an.graphql.reverse.ast;

/**
 */
public interface NodeVisitor<TParam,TRes> {
	
    TRes visitArgument(Argument node, TParam data);

    TRes visitArrayValue(ArrayValue node, TParam data);

    TRes visitBooleanValue(BooleanValue node, TParam data);

    TRes visitDirective(Directive node, TParam data);

    TRes visitDirectiveDefinition(DirectiveDefinition node, TParam data);

    TRes visitDirectiveLocation(DirectiveLocation node, TParam data);

    TRes visitDocument(Document node, TParam data);

    TRes visitEnumTypeDefinition(EnumTypeDefinition node, TParam data);

    TRes visitEnumValue(EnumValue node, TParam data);

    TRes visitEnumValueDefinition(EnumValueDefinition node, TParam data);

    TRes visitField(Field node, TParam data);

    TRes visitFieldDefinition(FieldDefinition node, TParam data);

    TRes visitFloatValue(FloatValue node, TParam data);

    TRes visitFragmentDefinition(FragmentDefinition node, TParam data);

    TRes visitFragmentSpread(FragmentSpread node, TParam data);

    TRes visitInlineFragment(InlineFragment node, TParam data);

    TRes visitInputObjectTypeDefinition(InputObjectTypeDefinition node, TParam data);

    TRes visitInputValueDefinition(InputValueDefinition node, TParam data);

    TRes visitIntValue(IntValue node, TParam data);

    TRes visitInterfaceTypeDefinition(InterfaceTypeDefinition node, TParam data);

    TRes visitListType(ListType node, TParam data);

    TRes visitNonNullType(NonNullType node, TParam data);

    TRes visitNullValue(NullValue node, TParam data);

    TRes visitObjectField(ObjectField node, TParam data);

    TRes visitObjectTypeDefinition(ObjectTypeDefinition node, TParam data);

    TRes visitObjectValue(ObjectValue node, TParam data);

    TRes visitOperationDefinition(OperationDefinition node, TParam data);

    TRes visitOperationTypeDefinition(OperationTypeDefinition node, TParam data);

    TRes visitScalarTypeDefinition(ScalarTypeDefinition node, TParam data);

    TRes visitSchemaDefinition(SchemaDefinition node, TParam data);

    TRes visitSelectionSet(SelectionSet node, TParam data);

    TRes visitStringValue(StringValue node, TParam data);

    TRes visitTypeName(TypeName node, TParam data);

    TRes visitUnionTypeDefinition(UnionTypeDefinition node, TParam data);

    TRes visitVariableDefinition(VariableDefinition node, TParam data);

    TRes visitVariableReference(VariableReference node, TParam data);
}
