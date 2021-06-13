
package io.fabric8.kubernetes.api.model.authentication;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class TokenReview implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "authentication.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "TokenReview";
    public ObjectMeta metadata;
    public TokenReviewSpec spec;
    public TokenReviewStatus status;

}
