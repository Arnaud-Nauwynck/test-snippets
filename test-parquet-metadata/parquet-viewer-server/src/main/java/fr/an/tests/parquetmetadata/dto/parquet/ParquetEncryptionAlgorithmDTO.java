package fr.an.tests.parquetmetadata.dto.parquet;

import lombok.Data;

@Data
public class ParquetEncryptionAlgorithmDTO {
	// union 

	ParquetAesGcmV1 AESGCMV1;
	ParquetAesGcmCtrV1 AESGCMCTRV1;
	
	
	@Data
	public static class ParquetAesGcmV1 {
		/** AAD prefix **/
		byte[] aadPrefix;

		/** Unique file identifier part of AAD suffix **/
		byte[] aadFileUnique;

		/** In files encrypted with AAD prefix without storing it,
		 * readers must supply the prefix **/
		Boolean supplyAadPrefix;
	}

	@Data
	public static class ParquetAesGcmCtrV1 {
		/** AAD prefix **/
		byte[] aadPrefix;

		/** Unique file identifier part of AAD suffix **/
		byte[] aadFileUnique;

		/** In files encrypted with AAD prefix without storing it,
		 * readers must supply the prefix **/
		Boolean supplyAadPrefix;
	}

}
