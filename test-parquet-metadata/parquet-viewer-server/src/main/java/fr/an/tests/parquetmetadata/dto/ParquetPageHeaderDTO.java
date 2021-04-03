package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

@Data
public class ParquetPageHeaderDTO {
	
  /** the type of the page: indicates which of the *_header fields is set **/
  ParquetPageType type;

  /** Uncompressed page size in bytes (not including this header) **/
  int uncompressed_page_size;

  /** Compressed (and potentially encrypted) page size in bytes, not including this header **/
  int compressed_page_size;

  /** The 32bit CRC for the page, to be be calculated as follows:
   * - Using the standard CRC32 algorithm
   * - On the data only, i.e. this header should not be included. 'Data'
   *   hereby refers to the concatenation of the repetition levels, the
   *   definition levels and the column value, in this exact order.
   * - On the encoded versions of the repetition levels, definition levels and
   *   column values
   * - On the compressed versions of the repetition levels, definition levels
   *   and column values where possible;
   *   - For v1 data pages, the repetition levels, definition levels and column
   *     values are always compressed together. If a compression scheme is
   *     specified, the CRC shall be calculated on the compressed version of
   *     this concatenation. If no compression scheme is specified, the CRC
   *     shall be calculated on the uncompressed version of this concatenation.
   *   - For v2 data pages, the repetition levels and definition levels are
   *     handled separately from the data and are never compressed (only
   *     encoded). If a compression scheme is specified, the CRC shall be
   *     calculated on the concatenation of the uncompressed repetition levels,
   *     uncompressed definition levels and the compressed column values.
   *     If no compression scheme is specified, the CRC shall be calculated on
   *     the uncompressed concatenation.
   * If enabled, this allows for disabling checksumming in HDFS if only a few
   * pages need to be read.
   **/
  Integer crc;

  // Headers for page specific data.  One only will be set.
  ParquetDataPageHeaderDTO data_page_header;
  ParquetIndexPageHeaderDTO index_page_header;
  ParquetDictionaryPageHeaderDTO dictionary_page_header;
  ParquetDataPageHeaderV2DTO data_page_header_v2;

}