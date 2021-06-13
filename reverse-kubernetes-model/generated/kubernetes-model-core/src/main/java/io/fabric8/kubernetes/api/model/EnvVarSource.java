
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EnvVarSource implements KubernetesResource
{

    public ConfigMapKeySelector configMapKeyRef;
    public ObjectFieldSelector fieldRef;
    public ResourceFieldSelector resourceFieldRef;
    public SecretKeySelector secretKeyRef;

}
