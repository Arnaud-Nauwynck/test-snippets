
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class HTTPGetAction implements KubernetesResource
{

    public String host;
    public List<HTTPHeader> httpHeaders = new ArrayList<HTTPHeader>();
    public String path;
    public IntOrString port;
    public String scheme;

}
