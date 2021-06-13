
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeConfigStatus implements KubernetesResource
{

    public NodeConfigSource active;
    public NodeConfigSource assigned;
    public String error;
    public NodeConfigSource lastKnownGood;

}
