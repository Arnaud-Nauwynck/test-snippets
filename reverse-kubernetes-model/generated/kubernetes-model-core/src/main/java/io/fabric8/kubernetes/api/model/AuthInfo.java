
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class AuthInfo implements KubernetesResource
{

    public java.lang.String as;
    public List<java.lang.String> asGroups = new ArrayList<java.lang.String>();
    public Map<String, ArrayList<String>> asUserExtra;
    public AuthProviderConfig authProvider;
    public java.lang.String clientCertificate;
    public java.lang.String clientCertificateData;
    public java.lang.String clientKey;
    public java.lang.String clientKeyData;
    public ExecConfig exec;
    public List<NamedExtension> extensions = new ArrayList<NamedExtension>();
    public java.lang.String password;
    public java.lang.String token;
    public java.lang.String tokenFile;
    public java.lang.String username;

}
