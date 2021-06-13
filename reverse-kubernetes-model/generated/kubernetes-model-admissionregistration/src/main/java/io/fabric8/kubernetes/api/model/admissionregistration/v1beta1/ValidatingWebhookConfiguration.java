
package io.fabric8.kubernetes.api.model.admissionregistration.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class ValidatingWebhookConfiguration implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "admissionregistration.k8s.io/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ValidatingWebhookConfiguration";
    public ObjectMeta metadata;
    public List<ValidatingWebhook> webhooks = new ArrayList<ValidatingWebhook>();

}
