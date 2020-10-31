package fr.an.metastore.impl.model;

import java.util.List;

import fr.an.metastore.api.dto.CatalogDatabaseDTO;
import fr.an.metastore.api.dto.CatalogFunctionDTO;
import fr.an.metastore.api.dto.CatalogTableDTO;
import fr.an.metastore.api.dto.CatalogTablePartitionDTO;
import fr.an.metastore.impl.utils.MetastoreListUtils;

public class CatalogModel2DtoConverter<
	TDb extends DatabaseModel, 
	TTable extends TableModel, 
	TPart extends TablePartitionModel, 
	TFunc extends FunctionModel
	> {

	public CatalogDatabaseDTO toDbDTO(TDb src) {
		CatalogDatabaseDTO res = new CatalogDatabaseDTO();
		// TODO
		return res;
	}
	
	public CatalogTableDTO toTableDTO(TTable src) {
		CatalogTableDTO res = new CatalogTableDTO();
		// TODO 
		return res;
	}
	
	public CatalogTablePartitionDTO toTablePartitionDTO(TPart src, TTable table) {
		CatalogTablePartitionDTO res = new CatalogTablePartitionDTO();
		// TODO
		return res;
	}

	public List<CatalogTablePartitionDTO> toTablePartitionDTOs(List<TPart> src, TTable table) {
		return MetastoreListUtils.map(src, x -> toTablePartitionDTO(x, table));
	}
	
	public CatalogFunctionDTO toFunctionDTO(TFunc func) {
		CatalogFunctionDTO res = new CatalogFunctionDTO();
		// TODO
		return res;
	}

}
