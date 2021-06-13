
package io.fabric8.kubernetes.api.model.apiextensions.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class WebhookConversion implements KubernetesResource
{

    public WebhookClientConfig clientConfig;
    public List<String> conversionReviewVersions = new ArrayList<String>();

}
