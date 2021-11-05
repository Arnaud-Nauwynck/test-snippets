package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ClusterUserInfo
 */
@Data
public class ClusterUserInfo {

	// User who has started the RM
    protected String rmLoginUser;
    
    // User who has placed the request
    protected String requestedUser;

}
