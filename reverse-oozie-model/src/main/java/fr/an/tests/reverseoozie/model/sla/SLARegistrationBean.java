package fr.an.tests.reverseoozie.model.sla;

import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SLA_REGISTRATION")
public class SLARegistrationBean {

    @Id
    @Basic
    private String jobId;

    @Basic
    private String parentId = null;

    @Basic
    private String appName = null;

    @Basic
    private String appType = null;

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
    @Column(name = "user_name")
    private String user = null;

    @Basic
    private String upstreamApps = null;

    @Basic
    private String jobData = null;

    @Basic
    private String slaConfig = null;

    @Basic
    private String notificationMsg = null;

//    @Transient
//    private Map<String, String> slaConfigMap;
//
//    @Transient
//    private MessageType msgType;

}
