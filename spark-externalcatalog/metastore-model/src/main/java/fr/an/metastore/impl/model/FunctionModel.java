
package fr.an.metastore.impl.model;

import fr.an.metastore.api.immutable.ImmutableCatalogFunctionDef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data @EqualsAndHashCode(callSuper=true)
public class FunctionModel extends ModelElement {

	private final DatabaseModel db;
	
	private final String funcName;
	
	private ImmutableCatalogFunctionDef funcDef;
	

	// implements ModelElement
	// --------------------------------------------------------------------------------------------

	@Override
	public ModelElement getParent() {
		return db;
	}

	@Override
	public Object getParentField() {
		return DatabaseModel.DatabaseModelChildField.function;
	}

	@Override
	public String childId() {
		return funcName;
	}
	
}
