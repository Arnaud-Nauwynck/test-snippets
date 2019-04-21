/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.an.test.ambarijpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Represents entity to hold principal for keytab.
 * Ideally this entity must have natural PK based on ({@link #keytabPath}, {@link #principalName}, {@link #hostId}),
 * but {@link #hostId} in some cases can be null, and also this entity must be used in service mappings(this can
 * cause dup of {@link #keytabPath}, {@link #principalName} fields in related entities), so we have surrogate {@link #kkpId}
 * id and unique constraint on ({@link #keytabPath}, {@link #principalName}, {@link #hostId}).
 */
@Entity
@Table(name = "kerberos_keytab_principal")
@TableGenerator(name = "kkp_id_generator",
  table = "ambari_sequences",
  pkColumnName = "sequence_name",
  valueColumnName = "sequence_value",
  pkColumnValue = "kkp_id_seq"
)
@NamedQueries({
  @NamedQuery(
    name = "KerberosKeytabPrincipalEntity.findAll",
    query = "SELECT kkpe FROM KerberosKeytabPrincipalEntity kkpe"
  ),
  @NamedQuery(
    name = "KerberosKeytabPrincipalEntity.findByHostAndKeytab",
    query = "SELECT kkpe FROM KerberosKeytabPrincipalEntity kkpe WHERE kkpe.hostId=:hostId AND kkpe.keytabPath=:keytabPath"
  ),
  @NamedQuery(
    name = "KerberosKeytabPrincipalEntity.findByPrincipal",
    query = "SELECT kkpe FROM KerberosKeytabPrincipalEntity kkpe WHERE kkpe.principalName=:principalName"
  ),
  @NamedQuery(
    name = "KerberosKeytabPrincipalEntity.findByHost",
    query = "SELECT kkpe FROM KerberosKeytabPrincipalEntity kkpe WHERE kkpe.hostId=:hostId"
  ),
  @NamedQuery(
    name = "KerberosKeytabPrincipalEntity.findByHostKeytabAndPrincipal",
    query = "SELECT kkpe FROM KerberosKeytabPrincipalEntity kkpe WHERE kkpe.hostId=:hostId AND kkpe.keytabPath=:keytabPath AND kkpe.principalName=:principalName"
  ),
  @NamedQuery(
    name = "KerberosKeytabPrincipalEntity.findByKeytabAndPrincipalNullHost",
    query = "SELECT kkpe FROM KerberosKeytabPrincipalEntity kkpe WHERE kkpe.principalName=:principalName AND kkpe.keytabPath=:keytabPath AND kkpe.hostId IS NULL"
  )
})
public class KerberosKeytabPrincipalEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "kkp_id_generator")
  @Column(name = "kkp_id")
  private Long kkpId;

  @Column(name = "keytab_path", updatable = false, nullable = false)
  private String keytabPath;

  @Column(name = "principal_name", updatable = false, nullable = false)
  private String principalName;

  @Column(name = "host_id")
  private Long hostId;

  @Column(name = "is_distributed", nullable = false)
  private Integer isDistributed = 0;

  @ManyToOne
  @JoinColumn(name = "keytab_path", referencedColumnName = "keytab_path", updatable = false, nullable = false, insertable = false)
  private KerberosKeytabEntity kerberosKeytabEntity;

  @ManyToOne
  @JoinColumn(name = "host_id", referencedColumnName = "host_id", updatable = false, insertable = false)
  private HostEntity hostEntity;

  @ManyToOne
  @JoinColumn(name = "principal_name", referencedColumnName = "principal_name", updatable = false, nullable = false, insertable = false)
  private KerberosPrincipalEntity kerberosPrincipalEntity;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "kerberosKeytabPrincipalEntity", orphanRemoval = true)
  private List<KerberosKeytabServiceMappingEntity> serviceMapping = new ArrayList<>();

}
