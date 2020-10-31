package fr.an.metastore.api.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStatisticsDTO;
import fr.an.metastore.api.dto.CatalogTableDTO.CatalogStorageFormatDTO;
import fr.an.metastore.api.immutable.ImmutablePartitionSpec;
import lombok.Data;

@Data
public class CatalogTablePartitionDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	ImmutablePartitionSpec spec; // CatalogTypes.TablePartitionSpec,
	CatalogStorageFormatDTO storage;
	Map<String,String> parameters = new LinkedHashMap<>();
	long createTime = System.currentTimeMillis();
	long lastAccessTime = -1;
	CatalogStatisticsDTO stats;

}
