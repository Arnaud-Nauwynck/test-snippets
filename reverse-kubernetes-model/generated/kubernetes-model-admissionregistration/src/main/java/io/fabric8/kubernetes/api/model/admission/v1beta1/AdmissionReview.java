
package io.fabric8.kubernetes.api.model.admission.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class AdmissionReview implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "admission.k8s.io/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "AdmissionReview";
    public AdmissionRequest request;
    public AdmissionResponse response;

}
