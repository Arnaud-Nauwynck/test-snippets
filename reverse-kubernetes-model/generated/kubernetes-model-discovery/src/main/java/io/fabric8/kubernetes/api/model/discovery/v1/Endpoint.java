
package io.fabric8.kubernetes.api.model.discovery.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectReference;

@Generated("jsonschema2pojo")
public class Endpoint implements KubernetesResource
{

    public List<java.lang.String> addresses = new ArrayList<java.lang.String>();
    public EndpointConditions conditions;
    public Map<String, String> deprecatedTopology;
    public EndpointHints hints;
    public java.lang.String hostname;
    public java.lang.String nodeName;
    public ObjectReference targetRef;
    public java.lang.String zone;

}
