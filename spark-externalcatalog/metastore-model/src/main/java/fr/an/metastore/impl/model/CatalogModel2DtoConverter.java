package fr.an.metastore.impl.model;

import java.util.List;

import fr.an.metastore.api.dto.CatalogDatabaseDTO;
import fr.an.metastore.api.dto.CatalogFunctionDTO;
import fr.an.metastore.api.dto.CatalogTableDTO;
import fr.an.metastore.api.dto.CatalogTablePartitionDTO;
import fr.an.metastore.api.info.CatalogTablePartitionInfo;
import fr.an.metastore.api.utils.MetastoreListUtils;

public class CatalogModel2DtoConverter {

	public CatalogDatabaseDTO toDbDTO(DatabaseModel src) {
		CatalogDatabaseDTO res = new CatalogDatabaseDTO();
		// TODO
		return res;
	}
	
	public CatalogTableDTO toTableDTO(TableModel src) {
		CatalogTableDTO res = new CatalogTableDTO();
		// TODO 
		return res;
	}

	public CatalogTablePartitionInfo toTablePartitionInfo(TablePartitionModel src, TableModel table) {
		return new CatalogTablePartitionInfo(src.getDef(),
				src.getLastAccessTime(),
				src.getStats());
	}

	public List<CatalogTablePartitionInfo> toTablePartitionInfos(List<TablePartitionModel> src, TableModel table) {
		return MetastoreListUtils.map(src, x -> toTablePartitionInfo(x, table));
	}

	public CatalogTablePartitionDTO toTablePartitionDTO(TablePartitionModel src, TableModel table) {
		CatalogTablePartitionDTO res = new CatalogTablePartitionDTO();
		// TODO
		return res;
	}

	public List<CatalogTablePartitionDTO> toTablePartitionDTOs(List<TablePartitionModel> src, TableModel table) {
		return MetastoreListUtils.map(src, x -> toTablePartitionDTO(x, table));
	}
	
	public CatalogFunctionDTO toFunctionDTO(FunctionModel func) {
		CatalogFunctionDTO res = new CatalogFunctionDTO();
		// TODO
		return res;
	}

}
