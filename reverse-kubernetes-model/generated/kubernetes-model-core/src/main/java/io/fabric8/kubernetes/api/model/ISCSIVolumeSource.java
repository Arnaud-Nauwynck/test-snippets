
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ISCSIVolumeSource implements KubernetesResource
{

    public boolean chapAuthDiscovery;
    public boolean chapAuthSession;
    public String fsType;
    public String initiatorName;
    public String iqn;
    public String iscsiInterface;
    public int lun;
    public List<String> portals = new ArrayList<String>();
    public boolean readOnly;
    public LocalObjectReference secretRef;
    public String targetPortal;

}
