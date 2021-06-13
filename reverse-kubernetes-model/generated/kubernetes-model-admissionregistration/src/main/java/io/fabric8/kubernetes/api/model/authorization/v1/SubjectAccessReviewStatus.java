
package io.fabric8.kubernetes.api.model.authorization.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class SubjectAccessReviewStatus implements KubernetesResource
{

    public boolean allowed;
    public boolean denied;
    public String evaluationError;
    public String reason;

}
