package fr.an.tests.parquetmetadata.dto.parquet;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Description for file metadata
 */
@Data
public class ParquetFileInfoDTO {

  /** Version of this file **/
  int version;

  /** Parquet schema for this file.  This schema contains metadata for all the columns.
   * The schema is represented as a tree with a single root.  The nodes of the tree
   * are flattened to a list by doing a depth-first traversal.
   * The column metadata contains the path in the schema for that column which can be
   * used to map columns to nodes in the schema.
   * The first element is the root **/
  List<ParquetSchemaElementDTO> schema;

  /** Number of rows in this file **/
  long numRows;

  /** Row groups in this file **/
  List<ParquetRowGroupDTO> rowGroups;

  /** Optional key/value metadata **/
  Map<String,String> keyValueMetadata;

  /** String for application that wrote this file.  This should be in the format
   * <Application> version <App Version> (build <App Build Hash>).
   * e.g. impala version 1.0 (build 6cf94d29b2b7115df4de2c06e2ab4326d721eb55)
   **/
  String createdBy;

  /**
   * Sort order used for the minValue and maxValue fields of each column in
   * this file. Sort orders are listed in the order matching the columns in the
   * schema. The indexes are not necessary the same though, because only leaf
   * nodes of the schema are represented in the list of sort orders.
   *
   * Without columnOrders, the meaning of the minValue and maxValue fields is
   * undefined. To ensure well-defined behaviour, if minValue and maxValue are
   * written to a Parquet file, columnOrders must be written as well.
   *
   * The obsolete min and max fields are always sorted by signed comparison
   * regardless of columnOrders.
   */
  List<ParquetColumnOrderDTO> colOrders;

  /** 
   * Encryption algorithm. This field is set only in encrypted files
   * with plaintext footer. Files with encrypted footer store algorithm id
   * in FileCryptoMetaData structure.
   */
  ParquetEncryptionAlgorithmDTO encryptionAlgorithm;

  /** 
   * Retrieval metadata of key used for signing the footer. 
   * Used only in encrypted files with plaintext footer. 
   */ 
  byte[] footerSigningKeyMetadata;

}
