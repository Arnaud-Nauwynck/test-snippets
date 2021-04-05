package fr.an.tests.parquetmetadata.dto;

import java.util.List;

import fr.an.tests.parquetmetadata.dto.parquet.ParquetRowGroupDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * same as ParquetFileInfo ... but without shared metadata(schema, version,..)
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class ParquetDataFileInfoDTO {

	/** Number of rows in this file **/
	long numRows;

	/** Row groups in this file **/
	List<ParquetRowGroupDTO> rowGroups;

}
