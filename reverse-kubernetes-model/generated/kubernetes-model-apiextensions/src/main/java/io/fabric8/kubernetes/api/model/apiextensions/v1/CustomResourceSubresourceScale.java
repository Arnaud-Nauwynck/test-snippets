
package io.fabric8.kubernetes.api.model.apiextensions.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CustomResourceSubresourceScale implements KubernetesResource
{

    public String labelSelectorPath;
    public String specReplicasPath;
    public String statusReplicasPath;

}
