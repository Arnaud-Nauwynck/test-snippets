
package io.fabric8.kubernetes.api.model.admission.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.GroupVersionKind;
import io.fabric8.kubernetes.api.model.GroupVersionResource;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.authentication.UserInfo;

@Generated("jsonschema2pojo")
public class AdmissionRequest implements KubernetesResource
{

    public boolean dryRun;
    public GroupVersionKind kind;
    public String name;
    public String namespace;
    public HasMetadata object;
    public HasMetadata oldObject;
    public String operation;
    public HasMetadata options;
    public GroupVersionKind requestKind;
    public GroupVersionResource requestResource;
    public String requestSubResource;
    public GroupVersionResource resource;
    public String subResource;
    public String uid;
    public UserInfo userInfo;

}
