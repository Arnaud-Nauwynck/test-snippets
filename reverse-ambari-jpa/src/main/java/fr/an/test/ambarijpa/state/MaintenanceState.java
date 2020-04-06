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
package fr.an.test.ambarijpa.state;

/**
 * Indicates when a Service or Host participates in automated operations, and
 * if alerts are enabled.
 */
public enum MaintenanceState {
  /**
   * All group-targeted commands are available.  Alerts enabled.
   */
  OFF,
  /**
   * Target was explicitly put into maintenance state.  Alerts disabled.
   */
  ON,
  /**
   * Target is in maintenance, implied by parent service.
   */
  IMPLIED_FROM_SERVICE,
  /**
   * Target is in maintenance, implied by parent host.
   */
  IMPLIED_FROM_HOST,
  /**
   * Target is in maintenance, implied by parent service and host.
   */
  IMPLIED_FROM_SERVICE_AND_HOST
}
