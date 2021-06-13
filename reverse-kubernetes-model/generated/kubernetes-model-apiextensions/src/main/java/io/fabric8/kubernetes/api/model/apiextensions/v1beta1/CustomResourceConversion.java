
package io.fabric8.kubernetes.api.model.apiextensions.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CustomResourceConversion implements KubernetesResource
{

    public List<String> conversionReviewVersions = new ArrayList<String>();
    public String strategy;
    public WebhookClientConfig webhookClientConfig;

}
