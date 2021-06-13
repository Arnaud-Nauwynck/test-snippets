
package io.fabric8.kubernetes.api.model.certificates.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class CertificateSigningRequestList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "certificates.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest> items = new ArrayList<io.fabric8.kubernetes.api.model.certificates.v1.CertificateSigningRequest>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CertificateSigningRequestList";
    public ListMeta metadata;

}
