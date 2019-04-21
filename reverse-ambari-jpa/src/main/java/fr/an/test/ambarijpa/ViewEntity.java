package fr.an.test.ambarijpa;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entity representing a View.
 */
@Table(name = "viewmain")
@NamedQuery(name = "allViews",
    query = "SELECT view FROM ViewEntity view")
@Entity
public class ViewEntity {

  public static final String AMBARI_ONLY = "AMBARI-ONLY";

  /**
   * The unique view name.
   */
  @Id
  @Column(name = "view_name", nullable = false, insertable = true,
      updatable = false, unique = true, length = 100)
  private String name;

  /**
   * The public view name.
   */
  @Column
  @Basic
  private String label;

  /**
   * The view description.
   */
  @Column
  @Basic
  private String description;

  /**
   * The icon path.
   */
  @Column
  @Basic
  private String icon;

  /**
   * The big icon path.
   */
  @Column
  @Basic
  private String icon64;

  /**
   * The view version.
   */
  @Column
  @Basic
  private String version;

  /**
   * The view build number.
   */
  @Column
  @Basic
  private String build;

  /**
   * The view archive.
   */
  @Column
  @Basic
  private String archive;

  /**
   * The masker class for parameters.
   */
  @Column
  @Basic
  private String mask;

  /**
   * Indicates whether or not this is a system view.
   */
  @Column(name = "system_view")
  @Basic
  private Integer system;

  /**
  * The list of view parameters.
  */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "view")
  private Collection<ViewParameterEntity> parameters = new HashSet<>();

  /**
   * The list of view resources.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "view")
  private Collection<ViewResourceEntity> resources = new HashSet<>();

   /**
   * The list of view instances.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "view")
  private Collection<ViewInstanceEntity> instances = new HashSet<>();

  /**
   * The list of view permissions.
   */
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumns({
      @JoinColumn(name = "resource_type_id", referencedColumnName = "resource_type_id", nullable = false)
  })
  private Collection<PermissionEntity> permissions = new HashSet<>();

  /**
   * The resource type.
   */
  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumns({
      @JoinColumn(name = "resource_type_id", referencedColumnName = "resource_type_id", nullable = false)
  })
  private ResourceTypeEntity resourceType;

}
