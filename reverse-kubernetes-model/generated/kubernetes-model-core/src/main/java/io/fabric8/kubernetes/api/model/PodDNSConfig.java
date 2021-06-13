
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodDNSConfig implements KubernetesResource
{

    public List<String> nameservers = new ArrayList<String>();
    public List<PodDNSConfigOption> options = new ArrayList<PodDNSConfigOption>();
    public List<String> searches = new ArrayList<String>();

}
