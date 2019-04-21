package fr.an.test.ambarijpa;


import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Represents an admin permission.
 */
@Table(name = "adminpermission")
@Entity
@TableGenerator(name = "permission_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
    , pkColumnValue = "permission_id_seq"
    , initialValue = 100
)
@NamedQueries({
    @NamedQuery(name = "PermissionEntity.findByName", query = "SELECT p FROM PermissionEntity p WHERE p.permissionName = :permissionName"),
    @NamedQuery(name = "PermissionEntity.findByPrincipals", query = "SELECT p FROM PermissionEntity p WHERE p.principal IN :principalList")
})
public class PermissionEntity {

  /**
   * Admin permission id constants.
   */
  public static final int AMBARI_ADMINISTRATOR_PERMISSION = 1;
  public static final int CLUSTER_USER_PERMISSION = 2;
  public static final int CLUSTER_ADMINISTRATOR_PERMISSION = 3;
  public static final int VIEW_USER_PERMISSION = 4;

  /**
   * Admin permission name constants.
   */
  public static final String AMBARI_ADMINISTRATOR_PERMISSION_NAME = "AMBARI.ADMINISTRATOR";
  public static final String CLUSTER_ADMINISTRATOR_PERMISSION_NAME = "CLUSTER.ADMINISTRATOR";
  public static final String CLUSTER_OPERATOR_PERMISSION_NAME = "CLUSTER.OPERATOR";
  public static final String SERVICE_ADMINISTRATOR_PERMISSION_NAME = "SERVICE.ADMINISTRATOR";
  public static final String SERVICE_OPERATOR_PERMISSION_NAME = "SERVICE.OPERATOR";
  public static final String CLUSTER_USER_PERMISSION_NAME = "CLUSTER.USER";
  public static final String VIEW_USER_PERMISSION_NAME = "VIEW.USER";

  /**
   * The permission id.
   */
  @Id
  @Column(name = "permission_id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "permission_id_generator")
  private Integer id;


  /**
   * The permission name.
   */
  @Column(name = "permission_name")
  private String permissionName;

  /**
   * The permission's (descriptive) label
   */
  @Column(name = "permission_label")
  private String permissionLabel;

  /**
   * The permission's (admin)principal reference
   */
  @OneToOne
  @JoinColumns({
      @JoinColumn(name = "principal_id", referencedColumnName = "principal_id", nullable = false),
  })
  private PrincipalEntity principal;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "resource_type_id", referencedColumnName = "resource_type_id", nullable = false),
  })
  private ResourceTypeEntity resourceType;

  /**
   * The set of authorizations related to this permission.
   *
   * This value declares the granular details for which operations this PermissionEntity grants
   * access.
   */
  @ManyToMany
  @JoinTable(
      name = "permission_roleauthorization",
      joinColumns = {@JoinColumn(name = "permission_id")},
      inverseJoinColumns = {@JoinColumn(name = "authorization_id")}
  )
  private Set<RoleAuthorizationEntity> authorizations = new LinkedHashSet<>();

  /**
   * The permission's explicit sort order
   */
  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 1;

}
