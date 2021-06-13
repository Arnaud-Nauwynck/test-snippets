
package io.fabric8.kubernetes.api.model.certificates.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class CertificateSigningRequest implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "certificates.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CertificateSigningRequest";
    public ObjectMeta metadata;
    public CertificateSigningRequestSpec spec;
    public CertificateSigningRequestStatus status;

}
