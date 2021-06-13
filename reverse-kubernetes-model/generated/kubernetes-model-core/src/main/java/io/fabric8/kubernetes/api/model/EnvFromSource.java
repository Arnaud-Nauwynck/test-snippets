
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EnvFromSource implements KubernetesResource
{

    public ConfigMapEnvSource configMapRef;
    public String prefix;
    public SecretEnvSource secretRef;

}
