package fr.an.tests.reverseoozie.model;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Bean that contains all the information to start an action for a workflow
 * node.
 */
@Entity
@Table(name = "WF_ACTIONS")
public class WorkflowActionBean {

    @Id
    private String id;

    // synonym: jobId !!!!
    @Basic
    // @Index
    private String wfId;

    
    @Basic
    @Column(name = "external_child_ids")
    @Lob
    private String externalChildIDs;

    @Basic
    private String externalId;


    
    
    @Basic
    @Column(name = "created_time")
    private Timestamp createdTimeTS;

    @Basic
    // @Index
    @Column(name = "status")
    private String statusStr; // = WorkflowAction.Status.PREP.toString();

    @Basic
    @Column(name = "last_check_time")
    private Timestamp lastCheckTimestamp;

    @Basic
    @Column(name = "end_time")
    private Timestamp endTimestamp;

    @Basic
    @Column(name = "start_time")
    private Timestamp startTimestamp;

    @Basic
    @Column(length = 1024)
    private String executionPath;

    @Basic
    private int pending = 0;

    @Basic
    // @Index
    @Column(name = "pending_age")
    private Timestamp pendingAgeTimestamp;

    @Basic
    private String signalValue;

    @Basic
    private String logToken;

    @Basic
    @Lob
    private String slaXml;

    @Basic
    private String name;

    @Basic
    private String cred;

    @Basic
    private String type;

    @Basic
    @Lob
    private String conf;

    @Basic
    private int retries;

    @Basic
    private int userRetryCount;

    @Basic
    private int userRetryMax;

    @Basic
    private int userRetryInterval;

    @Basic
    private String transition;

    @Basic
    @Lob
    private String data;

    @Basic
    @Lob
    private String stats;

    @Basic
    private String externalStatus;

    @Basic
    private String trackerUri;

    @Basic
    private String consoleUrl;

    @Basic
    private String errorCode;

    @Column(length = 500)
    private String errorMessage;


}
