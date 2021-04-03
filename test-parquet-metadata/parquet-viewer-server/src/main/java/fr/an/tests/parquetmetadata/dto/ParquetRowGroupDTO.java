package fr.an.tests.parquetmetadata.dto;

import java.util.List;

import lombok.Data;

@Data
public class ParquetRowGroupDTO {

  /** Metadata for each column chunk in this row group.
   * This list must have the same order as the SchemaElement list in FileMetaData.
   **/
  List<ParquetColumnChunkDTO> columns;

  /** Total byte size of all the uncompressed column data in this row group **/
  long total_byte_size;

  /** Number of rows in this row group **/
  long num_rows;

  /** If set, specifies a sort ordering of the rows in this RowGroup.
   * The sorting columns can be a subset of all the columns.
   */
  List<ParquetSortingColumnDTO> sorting_columns;

  /** Byte offset from beginning of file to first page (data or dictionary)
   * in this row group **/
  Long file_offset;

  /** Total byte size of all compressed (and potentially encrypted) column data 
   *  in this row group **/
  Long total_compressed_size;
  
  /** Row group ordinal in the file **/
  Integer ordinal;

}
