package fr.an.tests.reverseoozie.model;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "WF_JOBS")
public class WorkflowJobBean {

    @Id
    private String id;

    @ManyToOne
    private WorkflowJobBean parent;

    @OneToMany
    private List<WorkflowActionBean> actions;


    
    @Basic
    // @Index
    private String externalId;

//    @Basic
//    // @Index
//    private String parentId;


    @Basic
    @Lob
    private String protoActionConf;

    @Basic
    private String logToken;

    @Basic
    // @Index
    @Column(name = "status")
    private String statusStr; // = WorkflowJob.Status.PREP.toString();

    @Basic
    // @Index
    @Column(name = "created_time")
    private java.sql.Timestamp createdTimestamp;

    @Basic
    @Column(name = "start_time")
    private java.sql.Timestamp startTimestamp;

    @Basic
    // @Index
    @Column(name = "end_time")
    private java.sql.Timestamp endTimestamp;

    @Basic
    // @Index
    @Column(name = "last_modified_time")
    private java.sql.Timestamp lastModifiedTimestamp;

    @Basic
    @Lob
    private byte[] wfInstance ;

    @Basic
    @Column(name = "sla_xml")
    @Lob
    private String slaXml;


    @Basic
    private String appName;

    @Basic
    private String appPath;

    @Basic
    @Lob
    private String conf;

    @Basic
    // @Index
    @Column(name = "user_name")
    private String user;

    @Basic
    @Column(name = "group_name")
    private String group;

    @Basic
    private int run = 1;

//    @Transient
//    private String consoleUrl;
//
}
