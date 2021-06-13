
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class RBDVolumeSource implements KubernetesResource
{

    public String fsType;
    public String image;
    public String keyring;
    public List<String> monitors = new ArrayList<String>();
    public String pool;
    public boolean readOnly;
    public LocalObjectReference secretRef;
    public String user;

}
