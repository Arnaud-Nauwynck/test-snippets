package fr.an.tests.reverseoozie.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "BUNDLE_ACTIONS")
@Data
public class BundleActionBean {

    @Id
    private String bundleActionId; // = jobId + "_" + coordName

    // @Index // alias for jobId ??
    private String bundleId;

    private String coordName;

//    @Basic
//    private String coordId; // CoordinatorJobBean.id
    @ManyToOne
    private CoordinatorJobBean coord;
    
    @Basic
    @Column(name = "status")
    private String statusStr;

    @Basic
    private int critical = 0;

    @Basic
    private int pending = 0;

    @Basic
    @Column(name = "last_modified_time")
    private java.sql.Timestamp lastModifiedTimestamp;

}
