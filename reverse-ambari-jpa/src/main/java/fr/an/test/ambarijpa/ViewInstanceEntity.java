package fr.an.test.ambarijpa;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import fr.an.test.ambarijpa.state.ClusterType;

/**
 * Represents an instance of a View.
 */
@Table(name = "viewinstance", uniqueConstraints =
  @UniqueConstraint(
    name = "UQ_viewinstance_name", columnNames = {"view_name", "name"}
  )
)
@NamedQueries({ @NamedQuery(
    name = "allViewInstances",
    query = "SELECT viewInstance FROM ViewInstanceEntity viewInstance"),
    @NamedQuery(
        name = "viewInstanceByResourceId",
        query = "SELECT viewInstance FROM ViewInstanceEntity viewInstance "
            + "WHERE viewInstance.resource.id=:resourceId"),
    @NamedQuery(
        name = "getResourceIdByViewInstance",
        query = "SELECT viewInstance.resource FROM ViewInstanceEntity viewInstance "
            + "WHERE viewInstance.viewName = :viewName AND viewInstance.name = :instanceName"), })

@TableGenerator(name = "view_instance_id_generator",
  table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
  , pkColumnValue = "view_instance_id_seq"
  , initialValue = 1
)
@Entity
public class ViewInstanceEntity {

  @Id
  @Column(name = "view_instance_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "view_instance_id_generator")
  private Long viewInstanceId;

  @Column(name = "view_name", nullable = false, insertable = false, updatable = false)
  private String viewName;

  /**
   * The instance name.
   */
  @Column(name = "name", nullable = false, insertable = true, updatable = false)
  private String name;

  /**
   * The public view instance name.
   */
  @Column
  @Basic
  private String label;

  @Column
  @Basic
  private String description;

  @Column(name = "cluster_handle", nullable = true)
  private Long clusterHandle;

  /**
   *  Cluster Type for cluster Handle
   */
  @Enumerated(value = EnumType.STRING)
  @Column(name = "cluster_type", nullable = false, length = 100)
  private ClusterType clusterType = ClusterType.LOCAL_AMBARI;

  /**
   * Visible flag.
   */
  @Column
  @Basic
  private char visible;

  /**
   * The icon path.
   */
  @Column
  @Basic
  private String icon;


  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumns({
          @JoinColumn(name = "short_url", referencedColumnName = "url_id", nullable = true)
  })
  private ViewURLEntity viewUrl;

  /**
   * The big icon path.
   */
  @Column
  @Basic
  private String icon64;

  /**
   * The XML driven instance flag.
   */
  @Column(name="xml_driven")
  @Basic
  private char xmlDriven = 'N';

  /**
   * Indicates whether or not to alter the names of the data store entities to
   * avoid db reserved word conflicts.
   */
  @Column(name = "alter_names", nullable = false)
  @Basic
  private Integer alterNames;

  /**
   * The instance properties.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "viewInstance")
  private Collection<ViewInstancePropertyEntity> properties = new HashSet<>();

  /**
   * The instance data.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "viewInstance")
  private Collection<ViewInstanceDataEntity> data = new HashSet<>();

  /**
   * The list of view entities.
   */
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "viewInstance")
  private Collection<ViewEntityEntity> entities = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "view_name", referencedColumnName = "view_name", nullable = false)
  private ViewEntity view;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumns({
      @JoinColumn(name = "resource_id", referencedColumnName = "resource_id", nullable = false)
  })
  private ResourceEntity resource;

}
