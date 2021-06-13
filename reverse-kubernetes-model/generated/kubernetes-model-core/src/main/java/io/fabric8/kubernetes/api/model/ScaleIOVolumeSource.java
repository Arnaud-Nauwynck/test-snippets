
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ScaleIOVolumeSource implements KubernetesResource
{

    public String fsType;
    public String gateway;
    public String protectionDomain;
    public boolean readOnly;
    public LocalObjectReference secretRef;
    public boolean sslEnabled;
    public String storageMode;
    public String storagePool;
    public String system;
    public String volumeName;

}
