package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.QueueAclsInfo
 */
@Data
public class QueueAclsInfo {

    protected ArrayList<QueueAclInfo> queueAcl = new ArrayList<>();

}
