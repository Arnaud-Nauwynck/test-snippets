
package io.fabric8.kubernetes.api.model.authorization.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class SubjectRulesReviewStatus implements KubernetesResource
{

    public String evaluationError;
    public boolean incomplete;
    public List<NonResourceRule> nonResourceRules = new ArrayList<NonResourceRule>();
    public List<ResourceRule> resourceRules = new ArrayList<ResourceRule>();

}
