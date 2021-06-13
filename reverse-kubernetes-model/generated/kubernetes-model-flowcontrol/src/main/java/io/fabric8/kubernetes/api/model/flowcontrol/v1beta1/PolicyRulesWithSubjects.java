
package io.fabric8.kubernetes.api.model.flowcontrol.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class PolicyRulesWithSubjects implements KubernetesResource
{

    public List<NonResourcePolicyRule> nonResourceRules = new ArrayList<NonResourcePolicyRule>();
    public List<ResourcePolicyRule> resourceRules = new ArrayList<ResourcePolicyRule>();
    public List<Subject> subjects = new ArrayList<Subject>();

}
