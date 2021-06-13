
package io.fabric8.kubernetes.api.model.admissionregistration.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class MutatingWebhookConfiguration implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "admissionregistration.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "MutatingWebhookConfiguration";
    public ObjectMeta metadata;
    public List<MutatingWebhook> webhooks = new ArrayList<MutatingWebhook>();

}
