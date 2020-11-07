package fr.an.metastore.impl.model;

import fr.an.metastore.api.immutable.ImmutableCatalogTablePartitionDef;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import fr.an.metastore.api.spi.ITablePartitionModel;
import fr.an.metastore.api.immutable.ImmutableCatalogTableDef.ImmutableCatalogTableStatistics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data @EqualsAndHashCode(callSuper=true)
@RequiredArgsConstructor
public class TablePartitionModel extends ModelElement implements ITablePartitionModel {

	public final DatabaseModel db() { return table.getDb(); }
	
	@Getter
	private final TableModel table;
	
	@Getter
	public ImmutableCatalogTablePartitionDef def;

	// not in immutable def
	private long lastAccessTime = -1;
    private ImmutableCatalogTableStatistics stats;
    
//	@Getter @Setter
//	CatalogTablePartition sparkDefinition;

    // --------------------------------------------------------------------------------------------

	public String getPartitionName() {
		return def.getPartitionName();
	}

	// implements ModelElement
	// --------------------------------------------------------------------------------------------

	@Override
	public ModelElement getParent() {
		return table;
	}

	@Override
	public Object getParentField() {
		return TableModel.TableModelChildField.partition;
	}

	@Override
	public String childId() {
		return def.getPartitionName();
	}

	public ImmutablePartitionSpec getSpec() {
		return def.getSpec();
	}

}
