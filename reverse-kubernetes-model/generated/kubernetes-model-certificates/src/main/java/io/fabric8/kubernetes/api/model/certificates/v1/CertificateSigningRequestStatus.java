
package io.fabric8.kubernetes.api.model.certificates.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CertificateSigningRequestStatus implements KubernetesResource
{

    public String certificate;
    public List<CertificateSigningRequestCondition> conditions = new ArrayList<CertificateSigningRequestCondition>();

}
