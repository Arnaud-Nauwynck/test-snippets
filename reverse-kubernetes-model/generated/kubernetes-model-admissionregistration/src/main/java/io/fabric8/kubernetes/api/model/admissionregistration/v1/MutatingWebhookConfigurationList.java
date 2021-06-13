
package io.fabric8.kubernetes.api.model.admissionregistration.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class MutatingWebhookConfigurationList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.admissionregistration.v1.MutatingWebhookConfiguration>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "admissionregistration.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.admissionregistration.v1.MutatingWebhookConfiguration> items = new ArrayList<io.fabric8.kubernetes.api.model.admissionregistration.v1.MutatingWebhookConfiguration>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "MutatingWebhookConfigurationList";
    public ListMeta metadata;

}
