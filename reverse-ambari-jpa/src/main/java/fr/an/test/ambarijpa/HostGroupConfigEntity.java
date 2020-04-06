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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.an.test.ambarijpa.HostGroupConfigEntity.HostGroupConfigEntityPK;

/**
 * Represents a blueprint host group configuration.
 */
@IdClass(HostGroupConfigEntityPK.class)
@Table(name = "hostgroup_configuration")
@Entity
public class HostGroupConfigEntity {

  @Id
  @Column(name = "blueprint_name", nullable = false, insertable = false, updatable = false)
  private String blueprintName;

  @Id
  @Column(name = "hostgroup_name", nullable = false, insertable = false, updatable = false)
  private String hostGroupName;

  @Id
  @Column(name = "type_name", nullable = false, insertable = true, updatable = false)
  private String type;

  /**
   * Composite primary key for HostGroupConfigEntity.
   */
  public static class HostGroupConfigEntityPK {

    @Id
    @Column(name = "blueprint_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String blueprintName;

    @Id
    @Column(name = "hostgroup_name", nullable = false, insertable = false, updatable = false)
    private String hostGroupName;

    @Id
    @Column(name = "type_name", nullable = false, insertable = true, updatable = false, length = 100)
    private String type;

  }

  
  @Column(name = "config_data", nullable = false, insertable = true, updatable = false)
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String configData;

  @Column(name = "config_attributes", nullable = true, insertable = true, updatable = false)
  @Basic(fetch = FetchType.LAZY)
  @Lob
  private String configAttributes;


  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "hostgroup_name", referencedColumnName = "name", nullable = false),
      @JoinColumn(name = "blueprint_name", referencedColumnName = "blueprint_name", nullable = false)
  })
  private HostGroupEntity hostGroup;

}
