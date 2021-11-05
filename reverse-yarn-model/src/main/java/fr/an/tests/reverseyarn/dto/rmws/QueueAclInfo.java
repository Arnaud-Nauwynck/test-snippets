package fr.an.tests.reverseyarn.dto.rmws;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.QueueAclInfo
 */
@Data
public class QueueAclInfo {

    protected String accessType;
    protected String accessControlList;

}
