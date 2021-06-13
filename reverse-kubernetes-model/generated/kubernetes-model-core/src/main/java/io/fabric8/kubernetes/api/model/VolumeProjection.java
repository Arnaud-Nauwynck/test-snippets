
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class VolumeProjection implements KubernetesResource
{

    public ConfigMapProjection configMap;
    public DownwardAPIProjection downwardAPI;
    public SecretProjection secret;
    public ServiceAccountTokenProjection serviceAccountToken;

}
