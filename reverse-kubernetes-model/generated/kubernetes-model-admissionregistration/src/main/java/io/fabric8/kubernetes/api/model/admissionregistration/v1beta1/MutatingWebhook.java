
package io.fabric8.kubernetes.api.model.admissionregistration.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;

@Generated("jsonschema2pojo")
public class MutatingWebhook implements KubernetesResource
{

    public List<String> admissionReviewVersions = new ArrayList<String>();
    public WebhookClientConfig clientConfig;
    public String failurePolicy;
    public String matchPolicy;
    public String name;
    public LabelSelector namespaceSelector;
    public LabelSelector objectSelector;
    public String reinvocationPolicy;
    public List<RuleWithOperations> rules = new ArrayList<RuleWithOperations>();
    public String sideEffects;
    public int timeoutSeconds;

}
