package fr.an.tests.parquetmetadata.dto.parquet;

/** Crypto metadata for files with encrypted footer **/
public class ParquetFileCryptoMetaDataDTO {

  /** 
   * Encryption algorithm. This field is only used for files
   * with encrypted footer. Files with plaintext footer store algorithm id
   * inside footer (FileMetaData structure).
   */
  ParquetEncryptionAlgorithmDTO encryptionAlgorithm;
    
  /** Retrieval metadata of key used for encryption of footer, 
   *  and (possibly) columns **/
  byte[] keyMetadata;

}
