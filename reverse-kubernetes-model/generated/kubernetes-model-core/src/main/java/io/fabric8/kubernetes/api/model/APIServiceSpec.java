
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class APIServiceSpec implements KubernetesResource
{

    public String caBundle;
    public String group;
    public int groupPriorityMinimum;
    public boolean insecureSkipTLSVerify;
    public ServiceReference service;
    public String version;
    public int versionPriority;

}
