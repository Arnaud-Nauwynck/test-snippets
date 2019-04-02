package fr.an.test.ambarijpa;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import fr.an.test.ambarijpa.state.RepoTag;

/**
 * Represents a Repository definition type.
 */
@Entity
@Table(name = "repo_definition")
@TableGenerator(name = "repo_definition_id_generator",
    table = "ambari_sequences",
    pkColumnName = "sequence_name",
    valueColumnName = "sequence_value",
    pkColumnValue = "repo_definition_id_seq"
)
public class RepoDefinitionEntity {
  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "repo_definition_id_generator")
  private Long id;

  /**
   * CollectionTable for RepoTag enum
   */
  @Enumerated(value = EnumType.STRING)
  @ElementCollection(targetClass = RepoTag.class)
  @CollectionTable(name = "repo_tags", joinColumns = @JoinColumn(name = "repo_definition_id"))
  @Column(name = "tag")
  private Set<RepoTag> repoTags = new HashSet<>();

  /**
   * many-to-one association to {@link RepoOsEntity}
   */
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "repo_os_id", nullable = false)
  private RepoOsEntity repoOs;

  @Column(name = "repo_name", nullable = false)
  private String repoName;

  @Column(name = "repo_id", nullable = false)
  private String repoID;

  @Column(name = "base_url", nullable = false)
  private String baseUrl;

  @Column(name = "mirrors")
  private String mirrors;

  @Column(name = "distribution")
  private String distribution;

  @Column(name = "components")
  private String components;

  @Column(name = "unique_repo", nullable = false)
  private short unique = 0;

  /**
   * CollectionTable for RepoTag enum
   */
//  @Experimental(feature = ExperimentalFeature.CUSTOM_SERVICE_REPOS,
//    comment = "Remove logic for handling custom service repos after enabling multi-mpack cluster deployment")
  @ElementCollection(targetClass = String.class)
  @CollectionTable(name = "repo_applicable_services", joinColumns = {@JoinColumn(name = "repo_definition_id")})
  @Column(name = "service_name")
  private List<String> applicableServices = new LinkedList<>();

}