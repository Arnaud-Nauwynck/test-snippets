package fr.an.tests.parquetmetadata.dto;

import lombok.Data;

@Data
public class ParquetEncryptionAlgorithmDTO {
	// union 

	ParquetAesGcmV1 AES_GCM_V1;
	ParquetAesGcmCtrV1 AES_GCM_CTR_V1;
	
	
	@Data
	public static class ParquetAesGcmV1 {
		/** AAD prefix **/
		byte[] aad_prefix;

		/** Unique file identifier part of AAD suffix **/
		byte[] aad_file_unique;

		/** In files encrypted with AAD prefix without storing it,
		 * readers must supply the prefix **/
		Boolean supply_aad_prefix;
	}

	@Data
	public static class ParquetAesGcmCtrV1 {
		/** AAD prefix **/
		byte[] aad_prefix;

		/** Unique file identifier part of AAD suffix **/
		byte[] aad_file_unique;

		/** In files encrypted with AAD prefix without storing it,
		 * readers must supply the prefix **/
		Boolean supply_aad_prefix;
	}

}
