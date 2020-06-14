package fr.an.tests.reverseoozie.model.sla;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class to store all the SLA related details (summary) per job
 */
@Entity
@Table(name = "SLA_SUMMARY")
public class SLASummaryBean {

    @Id
    @Basic
    private String jobId;

    @Basic
    // @Index
    private String parentId;

    @Basic
    // @Index
    private String appName;

    @Basic
    private String appType;

    @Basic
    // @Column(name = "user_name")
    private String user;

    @Basic
    @Column(name = "created_time")
    private Timestamp createdTimeTS = null;

    @Basic
    // @Index
    @Column(name = "nominal_time")
    private Timestamp nominalTimeTS = null;

    @Basic
    @Column(name = "expected_start")
    private Timestamp expectedStartTS = null;

    @Basic
    @Column(name = "expected_end")
    private Timestamp expectedEndTS = null;

    @Basic
    @Column(name = "expected_duration")
    private long expectedDuration = -1;

    @Basic
    @Column(name = "actual_start")
    private Timestamp actualStartTS = null;

    @Basic
    @Column(name = "actual_end")
    private Timestamp actualEndTS = null;

    @Basic
    @Column(name = "actual_duration")
    private long actualDuration = -1;

    @Basic
    private String jobStatus;

    @Basic
    private String eventStatus;

    @Basic
    private String slaStatus;

    @Basic
    // @Index
    @Column(name = "event_processed")
    private byte eventProcessed = 0;

    @Basic
    // @Index
    @Column(name = "last_modified")
    private Timestamp lastModifiedTS = null;

}
