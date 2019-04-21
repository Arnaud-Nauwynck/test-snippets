package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.ViewInstancePropertyEntity.ViewInstancePropertyEntityPK;

/**
 * Represents a property of a View instance.
 */
@javax.persistence.IdClass(ViewInstancePropertyEntityPK.class)
@Table(name = "viewinstanceproperty")
@Entity
public class ViewInstancePropertyEntity {

  @Id
  @Column(name = "view_name", nullable = false, insertable = false, updatable = false)
  private String viewName;

  @Id
  @Column(name = "view_instance_name", nullable = false, insertable = false, updatable = false)
  private String viewInstanceName;

  @Id
  @Column(name = "name", nullable = false, insertable = true, updatable = false)
  private String name;

  /**
   * Composite primary key for ViewInstanceDataEntity.
   */
  public static class ViewInstancePropertyEntityPK {

    @Id
    @Column(name = "view_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String viewName;

    @Id
    @Column(name = "view_instance_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String viewInstanceName;

    @Id
    @Column(name = "name", nullable = false, insertable = true, updatable = false, length = 100)
    private String name;
  }
  

  @Column
  @Basic
  private String value;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "view_name", referencedColumnName = "view_name", nullable = false),
      @JoinColumn(name = "view_instance_name", referencedColumnName = "name", nullable = false)
  })
  private ViewInstanceEntity viewInstance;

}
