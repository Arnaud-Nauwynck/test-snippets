
package io.fabric8.kubernetes.api.model.discovery.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class EndpointSlice implements HasMetadata, Namespaced
{

    public String addressType;
    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "discovery.k8s.io/v1";
    public List<Endpoint> endpoints = new ArrayList<Endpoint>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "EndpointSlice";
    public ObjectMeta metadata;
    public List<EndpointPort> ports = new ArrayList<EndpointPort>();

}
