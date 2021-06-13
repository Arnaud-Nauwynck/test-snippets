
package io.fabric8.kubernetes.api.model.apiextensions.v1beta1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.databind.JsonNode;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class JSONSchemaProps implements KubernetesResource
{

    public java.lang.String $ref;
    public java.lang.String $schema;
    public JSONSchemaPropsOrBool additionalItems;
    public JSONSchemaPropsOrBool additionalProperties;
    public List<JSONSchemaProps> allOf = new ArrayList<JSONSchemaProps>();
    public List<JSONSchemaProps> anyOf = new ArrayList<JSONSchemaProps>();
    public JsonNode _default;
    public Map<String, io.fabric8.kubernetes.api.model.apiextensions.v1beta1.JSONSchemaProps> definitions;
    public Map<String, JSONSchemaPropsOrStringArray> dependencies;
    public java.lang.String description;
    public List<JsonNode> _enum = new ArrayList<JsonNode>();
    public JsonNode example;
    public boolean exclusiveMaximum;
    public boolean exclusiveMinimum;
    public ExternalDocumentation externalDocs;
    public java.lang.String format;
    public java.lang.String id;
    public JSONSchemaPropsOrArray items;
    public Long maxItems;
    public Long maxLength;
    public Long maxProperties;
    public double maximum;
    public Long minItems;
    public Long minLength;
    public Long minProperties;
    public double minimum;
    public double multipleOf;
    public JSONSchemaProps not;
    public boolean nullable;
    public List<JSONSchemaProps> oneOf = new ArrayList<JSONSchemaProps>();
    public java.lang.String pattern;
    public Map<String, io.fabric8.kubernetes.api.model.apiextensions.v1beta1.JSONSchemaProps> patternProperties;
    public Map<String, io.fabric8.kubernetes.api.model.apiextensions.v1beta1.JSONSchemaProps> properties;
    public List<java.lang.String> required = new ArrayList<java.lang.String>();
    public java.lang.String title;
    public java.lang.String type;
    public boolean uniqueItems;
    public boolean xKubernetesEmbeddedResource;
    public boolean xKubernetesIntOrString;
    public List<java.lang.String> xKubernetesListMapKeys = new ArrayList<java.lang.String>();
    public java.lang.String xKubernetesListType;
    public java.lang.String xKubernetesMapType;
    public boolean xKubernetesPreserveUnknownFields;

}
