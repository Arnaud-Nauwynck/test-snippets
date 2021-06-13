
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Config implements KubernetesResource
{

    public String apiVersion;
    public List<NamedCluster> clusters = new ArrayList<NamedCluster>();
    public List<NamedContext> contexts = new ArrayList<NamedContext>();
    public String currentContext;
    public List<NamedExtension> extensions = new ArrayList<NamedExtension>();
    public String kind;
    public Preferences preferences;
    public List<NamedAuthInfo> users = new ArrayList<NamedAuthInfo>();

}
