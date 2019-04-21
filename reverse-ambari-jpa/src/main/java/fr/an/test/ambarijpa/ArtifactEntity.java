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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import fr.an.test.ambarijpa.ArtifactEntity.ArtifactEntityPK;
/**
 * Entity representing an Artifact.
 */
@IdClass(ArtifactEntityPK.class)
@Table(name = "artifact")
@NamedQueries({
    @NamedQuery(name = "artifactByNameAndForeignKeys",
        query = "SELECT artifact FROM ArtifactEntity artifact " +
            "WHERE artifact.artifactName=:artifactName AND artifact.foreignKeys=:foreignKeys"),
    @NamedQuery(name = "artifactByName",
        query = "SELECT artifact FROM ArtifactEntity artifact " +
            "WHERE artifact.artifactName=:artifactName"),
    @NamedQuery(name = "artifactByForeignKeys",
        query = "SELECT artifact FROM ArtifactEntity artifact " +
            "WHERE artifact.foreignKeys=:foreignKeys")
})

@Entity
public class ArtifactEntity {
  @Id
  @Column(name = "artifact_name", nullable = false, insertable = true, updatable = false, unique = true)
  private String artifactName;

  @Id
  @Column(name = "foreign_keys", nullable = false, insertable = true, updatable = false)
  @Basic
  private String foreignKeys;

  /**
   * Composite primary key for ArtifactEntity.
   */
  public static class ArtifactEntityPK {
    @Id
    @Column(name = "artifact_name", nullable = false, insertable = true, updatable = false)
    private String artifactName;

    @Id
    @Column(name = "foreign_keys", nullable = false, insertable = true, updatable = false)
    private String foreignKeys;

  }

  
  @Column(name = "artifact_data", nullable = false, insertable = true, updatable = true)
  @Basic
  private String artifactData;

}
