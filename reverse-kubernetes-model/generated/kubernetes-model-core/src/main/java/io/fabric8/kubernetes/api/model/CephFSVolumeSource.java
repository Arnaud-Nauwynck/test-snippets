
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CephFSVolumeSource implements KubernetesResource
{

    public List<String> monitors = new ArrayList<String>();
    public String path;
    public boolean readOnly;
    public String secretFile;
    public LocalObjectReference secretRef;
    public String user;

}
