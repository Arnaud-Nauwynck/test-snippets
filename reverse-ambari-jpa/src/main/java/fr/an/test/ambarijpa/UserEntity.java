package fr.an.test.ambarijpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_name"})})
@Entity
@NamedQueries({
    @NamedQuery(name = "userByName", query = "SELECT user_entity from UserEntity user_entity " +
        "where lower(user_entity.userName)=lower(:username)")
})
@TableGenerator(name = "user_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
    , pkColumnValue = "user_id_seq"
    , initialValue = 2
    , allocationSize = 500
)
public class UserEntity {

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_id_generator")
  private Integer userId;

  @Column(name = "user_name", nullable = false)
  private String userName;

  @Column(name = "create_time", nullable = false)
  @Basic
  private long createTime;

  @Column(name = "active", nullable = false)
  private Integer active = 1;

  @Column(name = "consecutive_failures", nullable = false)
  private Integer consecutiveFailures = 0;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "local_username")
  private String localUsername;

  @Version
  @Column(name = "version")
  private Long version;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<MemberEntity> memberEntities = new HashSet<>();

  @OneToOne
  @JoinColumns({
      @JoinColumn(name = "principal_id", referencedColumnName = "principal_id", nullable = false)
  })
  private PrincipalEntity principal;

  @Column(name = "active_widget_layouts")
  private String activeWidgetLayouts;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<UserAuthenticationEntity> authenticationEntities = new ArrayList<>();

}
