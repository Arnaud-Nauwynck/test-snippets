package fr.an.tests.reverseyarn.dto.rmws;

import java.util.HashMap;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.CredentialsInfo
 */
@Data
public class CredentialsInfo {

	HashMap<String, String> tokens;

	HashMap<String, String> secrets;

}
