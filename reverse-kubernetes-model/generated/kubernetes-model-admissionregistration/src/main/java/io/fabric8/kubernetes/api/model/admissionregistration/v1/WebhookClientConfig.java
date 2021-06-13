
package io.fabric8.kubernetes.api.model.admissionregistration.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class WebhookClientConfig implements KubernetesResource
{

    public String caBundle;
    public ServiceReference service;
    public String url;

}
