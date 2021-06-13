
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Toleration implements KubernetesResource
{

    public String effect;
    public String key;
    public String operator;
    public Long tolerationSeconds;
    public String value;

}
