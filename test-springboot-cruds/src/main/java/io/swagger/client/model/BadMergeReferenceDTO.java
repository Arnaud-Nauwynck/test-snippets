/*
 * OpenAPI definition
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: v0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * BadMergeReferenceDTO
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-01-30T22:53:54.831415700+01:00[Europe/Paris]")
public class BadMergeReferenceDTO {
  @SerializedName("id")
  private Long id = null;

  @SerializedName("displayName")
  private String displayName = null;

  @SerializedName("field1")
  private String field1 = null;

  @SerializedName("field2")
  private String field2 = null;

  @SerializedName("field3")
  private String field3 = null;

  @SerializedName("field4")
  private String field4 = null;

  @SerializedName("field5")
  private String field5 = null;

  public BadMergeReferenceDTO id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @Schema(description = "")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BadMergeReferenceDTO displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

   /**
   * Get displayName
   * @return displayName
  **/
  @Schema(description = "")
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public BadMergeReferenceDTO field1(String field1) {
    this.field1 = field1;
    return this;
  }

   /**
   * Get field1
   * @return field1
  **/
  @Schema(description = "")
  public String getField1() {
    return field1;
  }

  public void setField1(String field1) {
    this.field1 = field1;
  }

  public BadMergeReferenceDTO field2(String field2) {
    this.field2 = field2;
    return this;
  }

   /**
   * Get field2
   * @return field2
  **/
  @Schema(description = "")
  public String getField2() {
    return field2;
  }

  public void setField2(String field2) {
    this.field2 = field2;
  }

  public BadMergeReferenceDTO field3(String field3) {
    this.field3 = field3;
    return this;
  }

   /**
   * Get field3
   * @return field3
  **/
  @Schema(description = "")
  public String getField3() {
    return field3;
  }

  public void setField3(String field3) {
    this.field3 = field3;
  }

  public BadMergeReferenceDTO field4(String field4) {
    this.field4 = field4;
    return this;
  }

   /**
   * Get field4
   * @return field4
  **/
  @Schema(description = "")
  public String getField4() {
    return field4;
  }

  public void setField4(String field4) {
    this.field4 = field4;
  }

  public BadMergeReferenceDTO field5(String field5) {
    this.field5 = field5;
    return this;
  }

   /**
   * Get field5
   * @return field5
  **/
  @Schema(description = "")
  public String getField5() {
    return field5;
  }

  public void setField5(String field5) {
    this.field5 = field5;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BadMergeReferenceDTO badMergeReferenceDTO = (BadMergeReferenceDTO) o;
    return Objects.equals(this.id, badMergeReferenceDTO.id) &&
        Objects.equals(this.displayName, badMergeReferenceDTO.displayName) &&
        Objects.equals(this.field1, badMergeReferenceDTO.field1) &&
        Objects.equals(this.field2, badMergeReferenceDTO.field2) &&
        Objects.equals(this.field3, badMergeReferenceDTO.field3) &&
        Objects.equals(this.field4, badMergeReferenceDTO.field4) &&
        Objects.equals(this.field5, badMergeReferenceDTO.field5);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, displayName, field1, field2, field3, field4, field5);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BadMergeReferenceDTO {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    displayName: ").append(toIndentedString(displayName)).append("\n");
    sb.append("    field1: ").append(toIndentedString(field1)).append("\n");
    sb.append("    field2: ").append(toIndentedString(field2)).append("\n");
    sb.append("    field3: ").append(toIndentedString(field3)).append("\n");
    sb.append("    field4: ").append(toIndentedString(field4)).append("\n");
    sb.append("    field5: ").append(toIndentedString(field5)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
