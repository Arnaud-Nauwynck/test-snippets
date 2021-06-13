
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EnvVar implements KubernetesResource
{

    public String name;
    public String value;
    public EnvVarSource valueFrom;

}
