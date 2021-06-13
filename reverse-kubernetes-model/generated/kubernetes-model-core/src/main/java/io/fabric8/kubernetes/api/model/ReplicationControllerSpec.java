
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ReplicationControllerSpec implements KubernetesResource
{

    public int minReadySeconds;
    public int replicas;
    public Map<String, String> selector;
    public PodTemplateSpec template;

}
