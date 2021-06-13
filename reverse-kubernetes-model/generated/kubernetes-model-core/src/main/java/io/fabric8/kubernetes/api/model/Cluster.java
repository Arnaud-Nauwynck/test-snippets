
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Cluster implements KubernetesResource
{

    public String certificateAuthority;
    public String certificateAuthorityData;
    public List<NamedExtension> extensions = new ArrayList<NamedExtension>();
    public boolean insecureSkipTlsVerify;
    public String proxyUrl;
    public String server;
    public String tlsServerName;

}
