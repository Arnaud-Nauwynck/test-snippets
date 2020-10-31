package fr.an.metastore.impl.model;

import java.util.LinkedHashMap;
import java.util.Map;

import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data @EqualsAndHashCode(callSuper=true)
@RequiredArgsConstructor
public class TablePartitionModel extends ModelElement {

	public final DatabaseModel db() { return table.getDb(); }
	
	@Getter
	private final TableModel table;
	
	@Getter
	private final ImmutablePartitionSpec spec;

	@Getter
	private final String partitionName;
		
	// TODO
//	@Getter @Setter
//	CatalogTablePartition sparkDefinition;
//    spec: CatalogTypes.TablePartitionSpec,
//    storage: CatalogStorageFormat,
    private final Map<String,String> parameters = new LinkedHashMap<String, String>();
    private long createTime = System.currentTimeMillis();
    private long lastAccessTime = -1;
//    stats: Option[CatalogStatistics] = None;

    	
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
		return partitionName;
	}

}
