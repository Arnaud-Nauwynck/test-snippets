/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import fr.an.test.ambarijpa.AmbariConfigurationEntity.AmbariConfigurationEntityPK;

@Table(name = "ambari_configuration")
@NamedQueries({
    @NamedQuery(
        name = "AmbariConfigurationEntity.findByCategory",
        query = "select ace from AmbariConfigurationEntity ace where ace.categoryName = :categoryName"),
    @NamedQuery(
        name = "AmbariConfigurationEntity.deleteByCategory",
        query = "delete from AmbariConfigurationEntity ace where ace.categoryName = :categoryName")
})
@IdClass(AmbariConfigurationEntityPK.class)
@Entity
public class AmbariConfigurationEntity {

  @Id
  @Column(name = "category_name")
  private String categoryName;

  @Id
  @Column(name = "property_name")
  private String propertyName;

  public static class AmbariConfigurationEntityPK implements Serializable {

	  private String categoryName;
	  private String propertyName;

  }

  
  @Column(name = "property_value")
  private String propertyValue;

}
