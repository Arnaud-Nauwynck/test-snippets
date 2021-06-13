
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class APIService implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apiregistration.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "APIService";
    public ObjectMeta metadata;
    public APIServiceSpec spec;
    public APIServiceStatus status;

}
