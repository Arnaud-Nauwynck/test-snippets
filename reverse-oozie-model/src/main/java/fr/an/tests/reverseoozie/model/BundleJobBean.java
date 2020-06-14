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
@Table(name = "BUNDLE_JOBS")
public class BundleJobBean {

    @Id
    private String id;

    @OneToMany
    private List<CoordinatorJobBean> coordJobs;

    @Basic
    private String appPath;

    @Basic
    @Column(name = "app_name")
    private String appName;

    @Basic
    @Column(name = "external_id")
    private String externalId;

    @Basic
    @Lob
    private String conf;

    @Basic
    private int timeOut = 0;

    @Basic
    // @Index
    @Column(name = "user_name")
    private String user;

    @Basic
    @Column(name = "group_name")
    private String group;

    @Transient
    private String consoleUrl;

    @Basic
    // @Index
    @Column(name = "status")
    private String statusStr; // = Job.Status.PREP.toString();

    @Basic
    @Column(name = "kickoff_time")
    private java.sql.Timestamp kickoffTimestamp;

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
    private String timeUnitStr; // = BundleJob.Timeunit.NONE.toString();

    @Basic
    @Column(name = "pending")
    private int pending = 0;

    @Basic
    // @Index
    @Column(name = "last_modified_time")
    private java.sql.Timestamp lastModifiedTimestamp;

    @Basic
    // @Index
    @Column(name = "suspended_time")
    private java.sql.Timestamp suspendedTimestamp;

    @Basic
    @Column(name = "job_xml")
    @Lob
    private String jobXml;

    @Basic
    @Column(name = "orig_job_xml")
    @Lob
    private String origJobXml;


}
