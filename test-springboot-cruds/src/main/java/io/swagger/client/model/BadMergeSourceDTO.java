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
import io.swagger.client.model.BadMergeReferenceIdDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * BadMergeSourceDTO
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-01-30T22:53:54.831415700+01:00[Europe/Paris]")
public class BadMergeSourceDTO {
  @SerializedName("id")
  private Long id = null;

  @SerializedName("ref")
  private BadMergeReferenceIdDTO ref = null;

  @SerializedName("field1")
  private String field1 = null;

  @SerializedName("field2")
  private String field2 = null;

  public BadMergeSourceDTO id(Long id) {
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

  public BadMergeSourceDTO ref(BadMergeReferenceIdDTO ref) {
    this.ref = ref;
    return this;
  }

   /**
   * Get ref
   * @return ref
  **/
  @Schema(description = "")
  public BadMergeReferenceIdDTO getRef() {
    return ref;
  }

  public void setRef(BadMergeReferenceIdDTO ref) {
    this.ref = ref;
  }

  public BadMergeSourceDTO field1(String field1) {
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

  public BadMergeSourceDTO field2(String field2) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BadMergeSourceDTO badMergeSourceDTO = (BadMergeSourceDTO) o;
    return Objects.equals(this.id, badMergeSourceDTO.id) &&
        Objects.equals(this.ref, badMergeSourceDTO.ref) &&
        Objects.equals(this.field1, badMergeSourceDTO.field1) &&
        Objects.equals(this.field2, badMergeSourceDTO.field2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ref, field1, field2);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BadMergeSourceDTO {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    ref: ").append(toIndentedString(ref)).append("\n");
    sb.append("    field1: ").append(toIndentedString(field1)).append("\n");
    sb.append("    field2: ").append(toIndentedString(field2)).append("\n");
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