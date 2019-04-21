package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.ViewInstanceDataEntity.ViewInstanceDataEntityPK;

/**
 * Represents a property of a View instance.
 */
@javax.persistence.IdClass(ViewInstanceDataEntityPK.class)
@Table(name = "viewinstancedata")
@Entity
public class ViewInstanceDataEntity {

  @Id
  @Column(name = "view_instance_id", nullable = false, insertable = false, updatable = false)
  private Long viewInstanceId;

  @Id
  @Column(name = "name", nullable = false, insertable = true, updatable = false)
  private String name;

  @Id
  @Column(name = "user_name", nullable = false, insertable = true, updatable = false)
  private String user;

  /**
   * Composite primary key for ViewInstanceDataEntity.
   */
  public static class ViewInstanceDataEntityPK {

    @Id
    @Column(name = "view_instance_id", nullable = false, insertable = false, updatable = false)
    private Long viewInstanceId;

    @Id
    @Column(name = "name", nullable = false, insertable = true, updatable = false, length = 100)
    private String name;

    @Id
    @Column(name = "user_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String user;

  }

  
  
  @Column(name = "view_name", nullable = false, insertable = false, updatable = false)
  private String viewName;

  @Column(name = "view_instance_name", nullable = false, insertable = false, updatable = false)
  private String viewInstanceName;

  @Column
  @Basic
  private String value;

  @ManyToOne
  @JoinColumns({
    @JoinColumn(name = "view_instance_id", referencedColumnName = "view_instance_id", nullable = false),
    @JoinColumn(name = "view_name", referencedColumnName = "view_name", nullable = false),
    @JoinColumn(name = "view_instance_name", referencedColumnName = "name", nullable = false)
  })
  private ViewInstanceEntity viewInstance;

}
