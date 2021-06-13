
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EndpointAddress implements KubernetesResource
{

    public String hostname;
    public String ip;
    public String nodeName;
    public ObjectReference targetRef;

}
