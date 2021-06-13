
package io.fabric8.kubernetes.api.model.authorization.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class SelfSubjectAccessReview implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "authorization.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "SelfSubjectAccessReview";
    public ObjectMeta metadata;
    public SelfSubjectAccessReviewSpec spec;
    public SubjectAccessReviewStatus status;

}
