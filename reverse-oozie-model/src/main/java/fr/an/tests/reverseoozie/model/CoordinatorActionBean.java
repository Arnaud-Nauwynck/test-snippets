package fr.an.tests.reverseoozie.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;


@Entity
@Table(name = "COORD_ACTIONS")
public class CoordinatorActionBean {

    @Id
    private String id;

    @Basic
    // @Index
    private String jobId;

    @Basic
    // @Index
    @Column(name = "status")
    private String statusStr; // = CoordinatorAction.Status.WAITING.toString();

    @Basic
    // @Index
    @Column(name = "nominal_time")
    private java.sql.Timestamp nominalTimestamp;

    @Basic
    // @Index
    @Column(name = "last_modified_time")
    private java.sql.Timestamp lastModifiedTimestamp;

    @Basic
    // @Index
    @Column(name = "created_time")
    private java.sql.Timestamp createdTimestamp;

    @Basic
    // @Index
    @Column(name = "rerun_time")
    private java.sql.Timestamp rerunTimestamp;

    @Basic
    // @Index
    private String externalId;

    @Basic
    @Lob
    private String slaXml;

    @Basic
    private int pending = 0;

    @Basic
    @Column(name = "job_type")
    private String type;

    @Basic
    private int actionNumber;

    @Basic
    @Lob
    private String createdConf;

    @Basic
    private int timeOut;

    @Basic
    @Lob
    private String runConf;

    @Basic
    @Lob
    private String actionXml;

    @Basic
    @Lob
    private String missingDependencies;

    @Basic
    @Lob
    private String pushMissingDependencies;

    @Basic
    private String externalStatus;

    @Basic
    private String trackerUri;

    @Basic
    private String consoleUrl;

    @Basic
    private String errorCode;

    @Basic
    private String errorMessage;

//    @Transient
//    private CoordInputDependency coordPushInputDependency;
//
//    @Transient
//    private CoordInputDependency coordPullInputDependency;

}
