package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "members", uniqueConstraints = {@UniqueConstraint(columnNames = {"group_id", "user_id"})})
@NamedQueries({
  @NamedQuery(name = "memberByUserAndGroup", query = "SELECT memberEnt FROM MemberEntity memberEnt where lower(memberEnt.user.userName)=:username AND lower(memberEnt.group.groupName)=:groupname")
})
@TableGenerator(name = "member_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "member_id_seq",
    initialValue = 1,
    allocationSize = 500
    )
public class MemberEntity {

	@Id
	@Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "member_id_generator")
	private Integer memberId;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private GroupEntity group;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;

}
