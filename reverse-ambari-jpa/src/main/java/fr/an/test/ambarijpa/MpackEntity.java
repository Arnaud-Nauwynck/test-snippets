package fr.an.test.ambarijpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;


/**
 * The {@link MpackEntity} class represents the mpack objects in the cluster.
 */

@Table(name = "mpacks")
@Entity
@TableGenerator(name = "mpack_id_generator", table = "ambari_sequences", pkColumnName = "sequence_name", valueColumnName = "sequence_value", pkColumnValue = "mpack_id_seq", initialValue = 1)
@NamedQueries({
        @NamedQuery(name = "MpackEntity.findById", query = "SELECT mpack FROM MpackEntity mpack where mpack.id = :id"),
        @NamedQuery(name = "MpackEntity.findAll", query = "SELECT mpack FROM MpackEntity mpack"),
        @NamedQuery(name = "MpackEntity.findByNameVersion", query = "SELECT mpack FROM MpackEntity mpack where mpack.mpackName = :mpackName and mpack.mpackVersion = :mpackVersion")})

public class MpackEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "mpack_id_generator")
  @Column(name = "id", nullable = false, updatable = false)
  private Long id;

  @Column(name = "registry_id", nullable = true, insertable = true, updatable = false, length = 10)
  private Long registryId;

  @Column(name = "mpack_name", nullable = false, updatable = true)
  private String mpackName;

  @Column(name = "mpack_version", nullable = false)
  private String mpackVersion;

  @Column(name = "mpack_uri", nullable = false)
  private String mpackUri;

}

