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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import fr.an.test.ambarijpa.ServiceDesiredStateEntity.ServiceDesiredStateEntityPK;
import fr.an.test.ambarijpa.state.MaintenanceState;
import fr.an.test.ambarijpa.state.State;

@javax.persistence.IdClass(ServiceDesiredStateEntityPK.class)
@javax.persistence.Table(name = "servicedesiredstate")
@Entity
public class ServiceDesiredStateEntity {

	@Id
	@Column(name = "cluster_id", nullable = false, insertable = false, updatable = false, length = 10)
	private Long clusterId;

	@Id
	@Column(name = "service_name", nullable = false, insertable = false, updatable = false)
	private String serviceName;

	@SuppressWarnings("serial")
	public static class ServiceDesiredStateEntityPK implements Serializable {
		@Column(name = "cluster_id", nullable = false, insertable = true, updatable = true, length = 10)
		@Id
		private Long clusterId;

		@Id
		@Column(name = "service_name", nullable = false, insertable = true, updatable = true, length = 32672, precision = 0)
		private String serviceName;

	}

	@Column(name = "desired_state", nullable = false, insertable = true, updatable = true)
	@Enumerated(value = EnumType.STRING)
	private State desiredState = State.INIT;

	@Basic
	@Column(name = "desired_host_role_mapping", nullable = false, insertable = true, updatable = true, length = 10)
	private int desiredHostRoleMapping = 0;

	@Column(name = "maintenance_state", nullable = false, insertable = true, updatable = true)
	@Enumerated(value = EnumType.STRING)
	private MaintenanceState maintenanceState = MaintenanceState.OFF;

	@Column(name = "credential_store_enabled", nullable = false, insertable = true, updatable = true)
	private short credentialStoreEnabled = 0;

	@OneToOne
	@JoinColumns({ @JoinColumn(name = "cluster_id", referencedColumnName = "cluster_id", nullable = false),
			@JoinColumn(name = "service_name", referencedColumnName = "service_name", nullable = false) })
	private ClusterServiceEntity clusterServiceEntity;

	/**
	 * The desired repository that the service should be on.
	 */
	@ManyToOne
	@JoinColumn(name = "desired_repo_version_id", unique = false, nullable = false, insertable = true, updatable = true)
	private RepositoryVersionEntity desiredRepositoryVersion;

}
