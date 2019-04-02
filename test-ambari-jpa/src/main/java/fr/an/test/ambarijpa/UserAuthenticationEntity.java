package fr.an.test.ambarijpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.UserAuthenticationType;

@Table(name = "user_authentication")
@Entity
@NamedQueries({
    @NamedQuery(name = "UserAuthenticationEntity.findAll",
        query = "SELECT entity FROM UserAuthenticationEntity entity"),
    @NamedQuery(name = "UserAuthenticationEntity.findByType",
        query = "SELECT entity FROM UserAuthenticationEntity entity where lower(entity.authenticationType)=lower(:authenticationType)"),
    @NamedQuery(name = "UserAuthenticationEntity.findByTypeAndKey",
        query = "SELECT entity FROM UserAuthenticationEntity entity where lower(entity.authenticationType)=lower(:authenticationType) and entity.authenticationKey=:authenticationKey"),
    @NamedQuery(name = "UserAuthenticationEntity.findByUser",
        query = "SELECT entity FROM UserAuthenticationEntity entity where entity.user.userId=:userId")
})
@TableGenerator(name = "user_authentication_id_generator",
    table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value"
    , pkColumnValue = "user_authentication_id_seq"
    , initialValue = 2
)
public class UserAuthenticationEntity {

  @Id
  @Column(name = "user_authentication_id")
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_authentication_id_generator")
  private Long userAuthenticationId;

  @Column(name = "authentication_type", nullable = false)
  @Enumerated(EnumType.STRING)
  @Basic
  private UserAuthenticationType authenticationType = UserAuthenticationType.LOCAL;

  @Column(name = "authentication_key")
  @Basic
  private String authenticationKey;

  @Column(name = "create_time", nullable = false)
  @Basic
  private long createTime;

  @Column(name = "update_time", nullable = false)
  @Basic
  private long updateTime ;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
  private UserEntity user;

}
