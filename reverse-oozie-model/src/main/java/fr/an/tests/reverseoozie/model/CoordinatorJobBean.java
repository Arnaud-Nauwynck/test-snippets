package fr.an.tests.reverseoozie.model;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "COORD_JOBS")
public class CoordinatorJobBean {

    @Id
    private String id;

    @OneToMany
    private List<CoordinatorActionBean> actions;

    @Basic
    private String externalId;

//    @ManyToOne
//    private BundleJobBean external;

    
    @Basic
    private String appPath;

    @Basic
    private String appName;

    @Basic
    @Lob
    private String conf;

    @Basic
    private String frequency = "0";

    @Basic
    private String timeZone;

    @Basic
    private int concurrency = 0;

    @Basic
    private int matThrottling = 0;

    @Basic
    private int timeOut = 0;

    @Basic
    private int lastActionNumber;

    @Basic
    // @Index
    private String user;

    @Basic
    @Column(name = "group_name")
    private String group;

    @Basic
    // @Index
    private String bundleId;

    @Transient
    private String consoleUrl;

    @Transient
    private int numActions = 0;

    @Basic
    // @Index
    @Column(name = "status")
    private String statusStr; // = CoordinatorJob.Status.PREP.toString();

    @Basic
    @Column(name = "start_time")
    private java.sql.Timestamp startTimestamp;

    @Basic
    // @Index
    @Column(name = "end_time")
    private java.sql.Timestamp endTimestamp;

    @Basic
    @Column(name = "pause_time")
    private java.sql.Timestamp pauseTimestamp;

    @Basic
    // @Index
    @Column(name = "created_time")
    private java.sql.Timestamp createdTimestamp;

    @Basic
    @Column(name = "time_unit")
    private String timeUnitStr; // = CoordinatorJob.Timeunit.NONE.toString();

    @Basic
    @Column(name = "execution")
    private String execution; // = CoordinatorJob.Execution.FIFO.toString();

    @Basic
    @Column(name = "last_action")
    private java.sql.Timestamp lastActionTimestamp;

    @Basic
    // @Index
    @Column(name = "next_matd_time")
    private java.sql.Timestamp nextMaterializedTimestamp;

    @Basic
    // @Index
    @Column(name = "last_modified_time")
    private java.sql.Timestamp lastModifiedTimestamp;

    @Basic
    // @Index
    @Column(name = "suspended_time")
    private java.sql.Timestamp suspendedTimestamp;

    @Basic
    @Lob
    private String jobXml;

    @Basic
    @Lob
    private String origJobXml;

    @Basic
    @Lob
    private String slaXml;

    @Basic
    private int pending = 0;

    @Basic
    private int doneMaterialization = 0;

    @Basic
    private String appNamespace;

}
