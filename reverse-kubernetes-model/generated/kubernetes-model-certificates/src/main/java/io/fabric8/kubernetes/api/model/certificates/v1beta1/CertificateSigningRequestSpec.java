
package io.fabric8.kubernetes.api.model.certificates.v1beta1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CertificateSigningRequestSpec implements KubernetesResource
{

    public Map<String, ArrayList<String>> extra;
    public List<java.lang.String> groups = new ArrayList<java.lang.String>();
    public java.lang.String request;
    public java.lang.String signerName;
    public java.lang.String uid;
    public List<java.lang.String> usages = new ArrayList<java.lang.String>();
    public java.lang.String username;

}
